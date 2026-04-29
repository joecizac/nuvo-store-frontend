# Karpathy behavioral guidelines

**Tradeoff:** Guidelines bias caution over speed. Use judgment for trivial tasks.

## 1. Think Before Coding

**No assumptions. No hidden confusion. Surface tradeoffs.**

Before implement:

- State assumptions explicit. If unsure, ask.
- Present multiple interpretations - no silent picks.
- If simpler approach exist, say so. Push back if warranted.
- If unclear, stop. Name confusion. Ask.

## 2. Simplicity First

**Minimum code solve problem. Nothing speculative.**

- No extra features.
- No abstractions for single-use.
- No unrequested flexibility.
- No error handling for impossible.
- Write 50 lines, not 200.

Ask: "Overcomplicated for senior engineer?" If yes, simplify.

## 3. Surgical Changes

**Touch only what must. Clean own mess.**

When edit existing:

- No "improvement" of adjacent code/format.
- No refactor of working code.
- Match existing style.
- Mention dead code, do not delete.

When create orphans:

- Remove unused imports/vars/fns from YOUR changes.
- Do not remove pre-existing dead code.

Test: Every line trace to request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks to goals:

- "Add validation" → "Tests for invalid input, then pass"
- "Fix bug" → "Test reproduce, then pass"
- "Refactor X" → "Tests pass before and after"

Multi-step plan:

```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong criteria allow independent looping.

---

# Jetpack Compose Expert

For Compose tasks, follow `skills/compose-expert/SKILL.md`.

Consult references before answer:

- State management -> `skills/compose-expert/references/state-management.md`
- Performance -> `skills/compose-expert/references/performance.md`
- Navigation -> `skills/compose-expert/references/navigation.md`
- (see SKILL.md for map)

Check source in `skills/compose-expert/references/source-code/`.

---
