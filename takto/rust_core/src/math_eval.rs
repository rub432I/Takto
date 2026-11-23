//! math_eval: Lightweight, zero-dependency offline math evaluator
//! Used by Takto bottom search bar for instant arithmetic calculations.

pub fn evaluate_expression(expr: &str) -> Option<f64> {
    let clean: String = expr.chars().filter(|c| !c.is_whitespace()).collect();
    if clean.is_empty() {
        return None;
    }

    // Basic tokenizer and recursive descent or precedence parsing
    let tokens = tokenize(&clean)?;
    let mut pos = 0;
    parse_addition(&tokens, &mut pos)
}

#[derive(Debug, PartialEq, Clone)]
enum Token {
    Number(f64),
    Plus,
    Minus,
    Multiply,
    Divide,
}

fn tokenize(s: &str) -> Option<Vec<Token>> {
    let mut tokens = Vec::new();
    let mut chars = s.chars().peekable();

    while let Some(&ch) = chars.peek() {
        match ch {
            '+' => { tokens.push(Token::Plus); chars.next(); }
            '-' => { tokens.push(Token::Minus); chars.next(); }
            '*' => { tokens.push(Token::Multiply); chars.next(); }
            '/' => { tokens.push(Token::Divide); chars.next(); }
            '0'..='9' | '.' => {
                let mut num_str = String::new();
                while let Some(&c) = chars.peek() {
                    if c.is_digit(10) || c == '.' {
                        num_str.push(c);
                        chars.next();
                    } else {
                        break;
                    }
                }
                let val: f64 = num_str.parse().ok()?;
                tokens.push(Token::Number(val));
            }
            _ => return None,
        }
    }
    Some(tokens)
}

fn parse_addition(tokens: &[Token], pos: &mut usize) -> Option<f64> {
    let mut val = parse_multiplication(tokens, pos)?;

    while *pos < tokens.len() {
        match tokens[*pos] {
            Token::Plus => {
                *pos += 1;
                let next = parse_multiplication(tokens, pos)?;
                val += next;
            }
            Token::Minus => {
                *pos += 1;
                let next = parse_multiplication(tokens, pos)?;
                val -= next;
            }
            _ => break,
        }
    }
    Some(val)
}

fn parse_multiplication(tokens: &[Token], pos: &mut usize) -> Option<f64> {
    let mut val = parse_primary(tokens, pos)?;

    while *pos < tokens.len() {
        match tokens[*pos] {
            Token::Multiply => {
                *pos += 1;
                let next = parse_primary(tokens, pos)?;
                val *= next;
            }
            Token::Divide => {
                *pos += 1;
                let next = parse_primary(tokens, pos)?;
                if next == 0.0 { return None; }
                val /= next;
            }
            _ => break,
        }
    }
    Some(val)
}

fn parse_primary(tokens: &[Token], pos: &mut usize) -> Option<f64> {
    if *pos >= tokens.len() {
        return None;
    }
    match tokens[*pos] {
        Token::Number(n) => {
            *pos += 1;
            Some(n)
        }
        Token::Minus => {
            *pos += 1;
            let val = parse_primary(tokens, pos)?;
            Some(-val)
        }
        _ => None,
    }
}
