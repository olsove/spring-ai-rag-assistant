# Architecture Decision Records (ADR)

## Purpose
Architecture Decision Records (ADRs) capture important architectural decisions, their context, and consequences.
They help document why a decision was made, which alternatives were considered, and what tradeoffs were accepted.
ADRs improve long-term maintainability by making decision history explicit for future you.

## When to Write an ADR
- Introducing a new dependency or framework.
- Choosing between architectural patterns or storage strategies.
- Making a decision that would be hard to reverse.
- Establishing a standard that impacts multiple components.

## Lifecycle
- **Proposed**: Drafted but not accepted yet.
- **Accepted**: Decision is made and should be implemented.
- **Superseded**: Replaced by a newer ADR.
- **Deprecated**: No longer applicable, but not replaced.

## Naming Standard
Use the following filename format:
```
NNNN-short-title.md
```

**Example**
```
0001-use-markdown-files.md
```

## ADR Template
```markdown
# ADR NNNN: Short Title

## Status
Proposed | Accepted | Superseded | Deprecated

## Date
YYYY-MM-DD

## Context
Describe the problem, constraints, and forces that led to this decision.

## Decision
State the decision clearly and succinctly.

## Alternatives Considered
- Option A: short reason it was not chosen.
- Option B: short reason it was not chosen.

## Consequences
- Positive impact.
- Negative impact.
- Tradeoffs or follow-up work.

## Related
- Links to related ADRs or documents.
```
