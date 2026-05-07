# OrbitLang

A domain-specific language (DSL) for defining satellite orbits and commanding spacecraft operations. OrbitLang allows mission designers to declaratively specify orbital parameters and write task-based command sequences in a readable, reliable syntax.

---

## Features

- Declarative orbit definitions (`LEO`, `MEO`, `GEO`, `HEO`)
- Task-based command sequencing
- Conditional logic with `if/else` and event-triggered `when` blocks
- Timed operations and repeating schedules
- Built-in satellite system variables (`battery`, `temperature`, `signal_strength`, `power_level`)
- Expression language with arithmetic and comparison operators
- Static type checking before execution
- Informative error messages with line numbers

---

## Project Structure

```
orbitlang/
├── lexer/           # Tokenizer — converts source text to token stream
├── parser/          # Recursive-descent parser, AST construction
├── typechecker/     # Static type checking (runs before interpretation)
├── interpreter/     # Tree-walk interpreter
├── examples/        # Sample .orbit programs
├── main.py          # Entry point
└── README.md
```

---

## Build & Run

**Requirements:** Python 3.10+

```bash
# Run a program
python main.py examples/mission.orbit

# Dump the AST without executing
python main.py examples/mission.orbit --dump-ast
```

---

## Language Overview

### Orbit Declaration

Define an orbit with its physical parameters:

```
orbit ISS {
  type: LEO
  altitude: 408 km
  inclination: 51.6 degrees
  eccentricity: 0.0001
}
```

### Task Definition

Group commands into named tasks:

```
task imaging {
  point camera to earth;
  capture photo every 30 seconds;
}
```

### Conditionals

Use `if/else` for value-based branching and `when` for event-triggered execution:

```
when signal_strength > 70 {
  transmit telemetry to groundstation;
}

if battery < 20 {
  enter safe_mode;
} else {
  activate science_payload;
}
```

### Timing

```
wait 10 minutes;

repeat every 1 hours {
  capture temperature_reading;
  transmit temperature_reading to groundstation;
}
```

### Expressions

Arithmetic and comparison expressions are supported in conditions:

```
if battery + 5 > power_level {
  deactivate antenna;
}
```

---

## Full Example

```
orbit ISS {
  type: LEO
  altitude: 408 km
  inclination: 51.6 degrees
  eccentricity: 0.0001
}

task imaging_pass {
  when signal_strength > 70 {
    point camera to earth;
    capture photo every 30 seconds;
  }

  if battery < 20 {
    enter safe_mode;
  } else {
    transmit photo to groundstation;
  }

  wait 5 minutes;
}

task housekeeping {
  repeat every 1 hours {
    capture telemetry;
    transmit telemetry to groundstation;
  }
}
```

---

## System Variables

These built-in variables reflect real-time satellite state and can be used in any expression:

| Variable          | Description                        |
|-------------------|------------------------------------|
| `battery`         | Current battery level (0–100)      |
| `temperature`     | On-board temperature reading       |
| `signal_strength` | Ground link signal strength (0–100)|
| `power_level`     | Available power output             |

---

## Error Handling

OrbitLang reports errors with line numbers:

```
[Line 12] TypeError: comparison between incompatible types
[Line 7]  ParseError: expected ';' after command
[Line 3]  RuntimeError: division by zero in expression
```

---

## EBNF Grammar (Summary)

```ebnf
program     = { declaration } ;
declaration = orbit_declaration | task_definition ;

orbit_declaration = "orbit" identifier "{" { orbit_parameter } "}" ;
task_definition   = "task"  identifier "{" { statement }       "}" ;

statement   = command_statement | conditional_statement | timing_statement | block ;

expression  = term { ("+" | "-") term } ;
term        = factor { ("*" | "/") factor } ;
factor      = "(" expression ")" | "-" factor | system_variable | number | identifier ;
```

Full grammar is documented in `docs/grammar.ebnf`.

---

## Course Context

Developed as a course project for **CSE 341 — Concepts of Programming Languages**, Gebze Technical University.

Covers lexical analysis, parsing (EBNF grammar), static scoping, type checking, and tree-walk interpretation as described in Sebesta's *Concepts of Programming Languages* (12th ed.), Chapters 1–7.
