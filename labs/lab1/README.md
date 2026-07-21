# Lab 1 - Mountain Hiking Challenge Registration

**Assignment:** J1.L.P0027

**Entry point:** `presentation.Program`

**Compatibility target:** Java 8

This console application manages mountain-hiking registrations. Its layered design separates the menu, business rules, entities, and persistence. The implementation covers validated registration CRUD, name search, fee calculation, statistics by mountain, and binary-file persistence.

## Engineering evidence

- Deep verification: **34 passed, 0 failed**.
- Explicit imports only; no wildcard or unresolved imports were found.
- Registration saves use a temporary file before replacing the destination.
- [Final report](../../output/pdf/Report_Lab_1_SE204330.pdf)
- [AI Audit Log](../../docs/ai-audit-logs/AI_Audit_Log_Lab_1_SE204330.xlsx)

From the repository root, run all verified checks with:

```powershell
.\scripts\verify-all.ps1
```

To run this application after compilation, use `presentation.Program` with this directory as the working directory so `MountainList.csv` is available.
