package com.takto.launcher.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * SafStorageManager: Pure native Android Storage Access Framework (SAF) document engine.
 * Reads, appends, and updates Logseq Markdown files under a user-persisted tree URI
 * without requiring AndroidX or DocumentFile libraries.
 */
public class SafStorageManager {

    public static Uri findOrCreateFile(ContentResolver cr, Uri treeUri, String subDirName, String fileName) {
        try {
            String treeDocId = DocumentsContract.getTreeDocumentId(treeUri);
            Uri rootDocUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, treeDocId);

            Uri parentDirUri = rootDocUri;
            if (subDirName != null && !subDirName.trim().isEmpty()) {
                parentDirUri = findOrCreateDirectory(cr, treeUri, rootDocUri, subDirName.trim());
            }

            if (parentDirUri == null) {
                return null;
            }

            // Search for existing file
            Uri existingFile = findChildDocument(cr, treeUri, parentDirUri, fileName);
            if (existingFile != null) {
                return existingFile;
            }

            // Create new file
            return DocumentsContract.createDocument(cr, parentDirUri, "text/markdown", fileName);
        } catch (Exception e) {
            System.err.println("SafStorageManager: findOrCreateFile failed for " + fileName + ": " + e.getMessage());
            return null;
        }
    }

    private static Uri findOrCreateDirectory(ContentResolver cr, Uri treeUri, Uri parentDocUri, String dirName) {
        try {
            Uri existing = findChildDocument(cr, treeUri, parentDocUri, dirName);
            if (existing != null) {
                return existing;
            }
            return DocumentsContract.createDocument(cr, parentDocUri, DocumentsContract.Document.MIME_TYPE_DIR, dirName);
        } catch (Exception e) {
            System.err.println("SafStorageManager: findOrCreateDirectory failed for " + dirName + ": " + e.getMessage());
            return null;
        }
    }

    public static Uri findChildDocument(ContentResolver cr, Uri treeUri, Uri parentDocUri, String targetName) {
        String parentDocId = DocumentsContract.getDocumentId(parentDocUri);
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocId);

        String[] projection = new String[]{
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
        };

        Cursor cursor = null;
        try {
            cursor = cr.query(childrenUri, projection, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String docId = cursor.getString(0);
                    String name = cursor.getString(1);
                    if (targetName.equalsIgnoreCase(name)) {
                        return DocumentsContract.buildDocumentUriUsingTree(treeUri, docId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("SafStorageManager: findChildDocument query failed: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static boolean appendBlock(ContentResolver cr, Uri docUri, String blockText) {
        if (docUri == null || blockText == null) return false;
        try (OutputStream os = cr.openOutputStream(docUri, "wa")) {
            if (os == null) return false;
            byte[] bytes = (blockText + "\n").getBytes("UTF-8");
            os.write(bytes);
            os.flush();
            return true;
        } catch (Exception e) {
            System.err.println("SafStorageManager: appendBlock failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateTaskStatus(ContentResolver cr, Uri docUri, String logseqId, String taskContent, boolean isDone) {
        if (docUri == null) return false;
        try {
            List<String> lines = readLines(cr, docUri);
            if (lines.isEmpty()) return false;

            boolean modified = false;
            String oldPrefix = isDone ? "- TODO " : "- DONE ";
            String newPrefix = isDone ? "- DONE " : "- TODO ";

            if (logseqId != null && !logseqId.trim().isEmpty()) {
                String targetIdLine = "id:: " + logseqId.trim();
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.contains(targetIdLine) && i > 0) {
                        String parentLine = lines.get(i - 1);
                        if (parentLine.trim().startsWith(oldPrefix)) {
                            lines.set(i - 1, parentLine.replaceFirst(java.util.regex.Pattern.quote(oldPrefix), newPrefix));
                            modified = true;
                            break;
                        }
                    }
                }
            }

            if (!modified && taskContent != null && !taskContent.trim().isEmpty()) {
                String exactTarget = oldPrefix + taskContent.trim();
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).trim().equals(exactTarget)) {
                        lines.set(i, lines.get(i).replaceFirst(java.util.regex.Pattern.quote(oldPrefix), newPrefix));
                        modified = true;
                        break;
                    }
                }
            }

            if (modified) {
                return writeAllLines(cr, docUri, lines);
            }
            return false;
        } catch (Exception e) {
            System.err.println("SafStorageManager: updateTaskStatus failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteEntry(ContentResolver cr, Uri docUri, String logseqId, String content) {
        if (docUri == null) return false;
        try {
            List<String> lines = readLines(cr, docUri);
            if (lines.isEmpty()) return false;

            List<String> remaining = new ArrayList<>();
            boolean modified = false;

            for (int i = 0; i < lines.size(); i++) {
                String current = lines.get(i);
                boolean matchesId = (logseqId != null && !logseqId.trim().isEmpty() &&
                        i + 1 < lines.size() && lines.get(i + 1).contains("id:: " + logseqId.trim()));
                boolean matchesExact = (content != null && !content.trim().isEmpty() &&
                        current.contains(content.trim()));

                if (matchesId) {
                    modified = true;
                    i++; // skip id:: child property
                    continue;
                } else if (matchesExact) {
                    modified = true;
                    if (i + 1 < lines.size() && lines.get(i + 1).trim().startsWith("id::")) {
                        i++;
                    }
                    continue;
                }
                remaining.add(current);
            }

            if (modified) {
                return writeAllLines(cr, docUri, remaining);
            }
            return false;
        } catch (Exception e) {
            System.err.println("SafStorageManager: deleteEntry failed: " + e.getMessage());
            return false;
        }
    }

    private static List<String> readLines(ContentResolver cr, Uri docUri) throws Exception {
        List<String> list = new ArrayList<>();
        try (InputStream is = cr.openInputStream(docUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String l;
            while ((l = reader.readLine()) != null) {
                list.add(l);
            }
        }
        return list;
    }

    private static boolean writeAllLines(ContentResolver cr, Uri docUri, List<String> lines) {
        try (OutputStream os = cr.openOutputStream(docUri, "wt")) {
            if (os == null) return false;
            for (String l : lines) {
                os.write((l + "\n").getBytes("UTF-8"));
            }
            os.flush();
            return true;
        } catch (Exception e) {
            System.err.println("SafStorageManager: writeAllLines failed: " + e.getMessage());
            return false;
        }
    }
}
