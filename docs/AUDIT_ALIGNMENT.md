# Report and AI Audit Log Alignment

Verified on **21 July 2026** against the source and executable deep-verification harnesses in this repository.

| Field | Lab 1 | Lab 2 | Lab 3 |
|---|---|---|---|
| Student | Nguyen Dinh Chuong / SE204330 | Nguyen Dinh Chuong / SE204330 | Nguyen Dinh Chuong / SE204330 |
| Course | LAB211 | LAB211 | LAB211 |
| Assignment | J1.L.P0027 - Mountain Hiking Challenge Registration | J1.L.P0036 - Football Club & Player Management | J1.L.P0037 - Employee Payroll Management System |
| Required functions | 9 | 14 | 9 |
| Runnable deep checks | 34 passed, 0 failed | 62 passed, 0 failed | 57 passed, 0 failed |
| Audit core prompts | 12 | 18 | 20 |
| Hallucination cases | 3 | 3 | 3 |

## Alignment rules applied

- Report section 2 contains UML that names only classes and interfaces present in the final source.
- Report section 3 explains algorithms implemented by the cited final methods.
- Report section 4 distinguishes archived regression evidence from the deep harness re-executed on 21 July 2026.
- Every Audit Log retains its Prompt, AI Response Summary, and Human Delta, and now prefixes its evidence with an `EV-*` identifier.
- Each Audit Log includes an Evidence Index with repository paths, a current test result, verification date, and SHA-256 hashes for the executable harness and recorded output.
- No screenshots or conversations were fabricated. Textual evidence points to inspectable artifacts in this repository.

## Important scope notes

- Lab 1 uses Java object serialization because the assignment explicitly requests object-format persistence.
- Lab 2 saves two files sequentially; each file replacement is protected, but the pair is not one cross-file transaction.
- Lab 3 uses soft delete because the lecturer's Lab 3 design note specifies `D_Soft`; the employee record is retained and its status becomes inactive.
