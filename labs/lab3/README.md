# Lab 3 - Employee Payroll Management System

**Assignment:** J1.L.P0037

**Entry point:** `app.Main`

**Compatibility target:** Java 8

This console application manages employee records and payroll. It demonstrates dependency injection, row-isolated CSV loading, validate-before-commit updates, defensive copies, soft delete, active-employee filtering, and payroll calculation.

## Engineering evidence

- Deep verification: **57 passed, 0 failed**.
- Explicit imports only; no wildcard or unresolved imports were found.
- Soft delete follows the lecturer's `D_Soft` note while preserving employee history.
- [Final report](../../output/pdf/Report_Lab_3_SE204330.pdf)
- [AI Audit Log](../../docs/ai-audit-logs/AI_Audit_Log_Lab_3_SE204330.xlsx)

From the repository root, run all verified checks with:

```powershell
.\scripts\verify-all.ps1
```

To run this application after compilation, use `app.Main` with this directory as the working directory so `employees.txt` is available.
