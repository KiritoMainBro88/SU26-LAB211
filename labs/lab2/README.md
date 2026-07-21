# Lab 2 - Football Club & Player Management

**Assignment:** J1.L.P0036

**Entry point:** `app.Main`

**Compatibility target:** Java 8

This console application manages football clubs and players. It uses generic manager abstractions, dependency-oriented interfaces, strict clubs-first CSV loading, validation, search, sorting, and safe per-file replacement during persistence.

## Engineering evidence

- Deep verification: **62 passed, 0 failed**.
- Explicit imports only; no wildcard or unresolved imports were found.
- CSV parsing supports quoted fields and rejects invalid relationships.
- [Final report](../../output/pdf/Report_Lab_2_SE204330.pdf)
- [AI Audit Log](../../docs/ai-audit-logs/AI_Audit_Log_Lab_2_SE204330.xlsx)

From the repository root, run all verified checks with:

```powershell
.\scripts\verify-all.ps1
```

To run this application after compilation, use `app.Main` with this directory as the working directory so `clubs.txt` and `players.txt` are available.
