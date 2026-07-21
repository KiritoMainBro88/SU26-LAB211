# Verification Record

**Verification date:** 21 July 2026

**Environment:** Windows 11, `javac 17.0.16` targeting Java 8 with `--release 8`, Java runtime `1.8.0_202`

**Command:** `scripts\verify-all.ps1`

## Results

| Project | Compilation | Deep verification | Result |
|---|---|---|---|
| Lab 1 | PASS, Java 8 target, `-Xlint:all` | `Lab1DeepVerification` | 34 passed, 0 failed |
| Lab 2 | PASS, Java 8 target, `-Xlint:all` | `Lab2DeepVerification` | 62 passed, 0 failed |
| Lab 3 | PASS, Java 8 target, `-Xlint:all` | `Lab3DeepVerification` | 57 passed, 0 failed |
| **Total** | **3/3 PASS** | **3/3 PASS** | **153 passed, 0 failed** |

## Import audit

- Unresolved or incorrect imports: **0** (all source and test files compiled).
- Wildcard imports: **0**.
- Definitely unused explicit imports: **0**, checked by removing import declarations and verifying that each imported simple type name is referenced in its compilation unit.

## Scope of the checks

The executable harnesses cover null dependencies, invalid IDs, numeric boundaries, `NaN`/`Infinity`, duplicate identities, defensive copies, update state preservation, CSV quoting, malformed rows, persistence failure, sorting, soft delete, and active-only payroll. See each `labs/lab*/testcases/` directory for the exact assertions and recorded output.

The archived `verification_results.txt` files are retained as earlier regression evidence. The current completion claim is based on the runnable deep-verification classes re-executed during this review.

After installing `requirements-docs.txt`, validate the generated documents and their cross-file evidence with:

```powershell
python .\scripts\validate-artifacts.py
```
