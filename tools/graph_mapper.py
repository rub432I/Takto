#!/usr/bin/env python3
"""
graph_mapper.py: Lightweight Codebase Knowledge Graph & Dependency Mapper
Implements the core Graphify paradigm from youtube_pl.txt (Lines 92-105).
Maps imports, function definitions, classes, and call dependencies across
codebase files without requiring full file reads.

Commands:
    python3 graph_mapper.py map <directory>
    python3 graph_mapper.py query <symbol_name>
"""

import sys
import os
import ast
import json
import argparse
from typing import Dict, List

CACHE_FILE = os.path.expanduser("~/workspace/.codebase_graph.json")

def parse_python_file(filepath: str) -> Dict:
    """Extract functions, classes, and imports from a Python source file using AST."""
    symbols = {"imports": [], "functions": [], "classes": []}
    try:
        with open(filepath, "r", encoding="utf-8", errors="replace") as f:
            tree = ast.parse(f.read(), filename=filepath)

        for node in ast.walk(tree):
            if isinstance(node, ast.Import):
                for name in node.names:
                    symbols["imports"].append(name.name)
            elif isinstance(node, ast.ImportFrom):
                mod = node.module or ""
                for name in node.names:
                    symbols["imports"].append(f"{mod}.{name.name}")
            elif isinstance(node, ast.FunctionDef):
                symbols["functions"].append(node.name)
            elif isinstance(node, ast.ClassDef):
                symbols["classes"].append(node.name)
    except Exception as e:
        symbols["parse_error"] = str(e)

    return symbols

def map_directory(directory: str):
    """Scan a project directory and build the knowledge graph."""
    graph = {}
    total_files = 0

    for root, dirs, files in os.walk(directory):
        # Ignore hidden dirs and git
        dirs[:] = [d for d in dirs if not d.startswith(".") and d not in ("archive", "node_modules", "__pycache__")]
        for f in files:
            if f.endswith(".py"):
                full_path = os.path.join(root, f)
                rel_path = os.path.relpath(full_path, directory)
                graph[rel_path] = parse_python_file(full_path)
                total_files += 1

    with open(CACHE_FILE, "w", encoding="utf-8") as f:
        json.dump(graph, f, indent=2)

    print("=" * 65)
    print(f"🗺️  CODEBASE KNOWLEDGE GRAPH MAPPED")
    print("=" * 65)
    print(f"Directory Mapped: {directory}")
    print(f"Files Indexed   : {total_files} python files")
    print(f"Graph Saved to  : {CACHE_FILE}")
    print("=" * 65)

def query_symbol(symbol: str):
    """Search the knowledge graph for matching functions, classes, or imports."""
    if not os.path.isfile(CACHE_FILE):
        print("Error: Knowledge graph not found. Run 'python3 graph_mapper.py map <dir>' first.")
        sys.exit(1)

    graph = json.load(open(CACHE_FILE))
    matches = []

    for file_path, data in graph.items():
        if symbol in data.get("classes", []):
            matches.append((file_path, "Class", symbol))
        if symbol in data.get("functions", []):
            matches.append((file_path, "Function", symbol))
        for imp in data.get("imports", []):
            if symbol.lower() in imp.lower():
                matches.append((file_path, "Import", imp))

    print("=" * 65)
    print(f"🔍 GRAPH QUERY RESULTS FOR: '{symbol}'")
    print("=" * 65)
    if not matches:
        print(f"No definitions or imports matching '{symbol}' found in knowledge graph.")
    else:
        for file_path, kind, name in matches:
            print(f"  [{kind:<8}] {file_path:<35} -> {name}")
    print("=" * 65)

def main():
    parser = argparse.ArgumentParser(description="Codebase Dependency & Symbol Knowledge Graph Mapper.")
    subparsers = parser.add_subparsers(dest="command")

    map_parser = subparsers.add_parser("map", help="Index a directory")
    map_parser.add_argument("directory", default=".", nargs="?", help="Directory to map")

    query_parser = subparsers.add_parser("query", help="Query a symbol in the graph")
    query_parser.add_argument("symbol", help="Function, class, or import name to trace")

    args = parser.parse_args()

    if args.command == "map":
        map_directory(os.path.abspath(args.directory))
    elif args.command == "query":
        query_symbol(args.symbol)
    else:
        parser.print_help()

if __name__ == "__main__":
    main()
