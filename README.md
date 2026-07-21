# SU26 LAB211 - Java OOP Portfolio

[![Java 8 verification](https://github.com/KiritoMainBro88/SU26-LAB211/actions/workflows/verify.yml/badge.svg)](https://github.com/KiritoMainBro88/SU26-LAB211/actions/workflows/verify.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Three console applications developed for FPT University's LAB211 course, presented as a verified software-engineering portfolio. The repository contains the latest reviewed source only; earlier submission folders, compiled outputs, and IDE-private files are intentionally excluded.

**Student:** Nguyen Dinh Chuong (Nguyễn Đình Chương)

**Student ID:** SE204330

**Course:** LAB211 - OOP with Java Lab

**Latest verification:** 21 July 2026

## Projects

| Lab | Assignment | Main engineering focus | Current executable verification |
|---|---|---|---:|
| [Lab 1](labs/lab1) | J1.L.P0027 - Mountain Hiking Challenge Registration | Layered OOP, binary persistence, validation, statistics | 34 passed, 0 failed |
| [Lab 2](labs/lab2) | J1.L.P0036 - Football Club & Player Management | Generic managers, interfaces, strict CSV loading, sorting | 62 passed, 0 failed |
| [Lab 3](labs/lab3) | J1.L.P0037 - Employee Payroll Management System | Dependency injection, resilient CSV loading, soft delete, payroll | 57 passed, 0 failed |

The current deep-verification total is **153 passed and 0 failed**. All three projects compile with Java 8 language/API compatibility. The source audit also found no wildcard imports, no unresolved imports, and no definitely unused explicit imports.

## Reports and AI transparency

Each report is a searchable, text-based document focused on the three areas requested by the lecturer: UML, algorithms, and test cases.

| Lab | Report (PDF) | Editable source | AI Audit Log |
|---|---|---|---|
| 1 | [Report Lab 1](output/pdf/Report_Lab_1_SE204330.pdf) | [DOCX](docs/editable/Report_Lab_1_SE204330.docx) | [XLSX](docs/ai-audit-logs/AI_Audit_Log_Lab_1_SE204330.xlsx) |
| 2 | [Report Lab 2](output/pdf/Report_Lab_2_SE204330.pdf) | [DOCX](docs/editable/Report_Lab_2_SE204330.docx) | [XLSX](docs/ai-audit-logs/AI_Audit_Log_Lab_2_SE204330.xlsx) |
| 3 | [Report Lab 3](output/pdf/Report_Lab_3_SE204330.pdf) | [DOCX](docs/editable/Report_Lab_3_SE204330.docx) | [XLSX](docs/ai-audit-logs/AI_Audit_Log_Lab_3_SE204330.xlsx) |

The Audit Logs record prompts, summarized AI responses, human decisions, hallucination checks, and a concrete Evidence Index. Evidence is linked to final source paths, executable test harnesses, verified results, and SHA-256 hashes. See [document alignment](docs/AUDIT_ALIGNMENT.md) for the cross-check.

Full checksums for the final PDF, DOCX, and XLSX deliverables are recorded in [SHA256SUMS.txt](evidence/SHA256SUMS.txt).

## Verify locally on Windows

Requirements: PowerShell and a JDK on `PATH`. JDK 8 and newer are supported; newer compilers use `--release 8` automatically.

```powershell
Set-Location "D:\study\SU26\LAB211\LAB211_github"
.\scripts\verify-all.ps1
```

The same script runs on every push and pull request through GitHub Actions.

The reports and Audit Logs can be regenerated with the pinned Python packages in `requirements-docs.txt`:

```powershell
python -m pip install -r .\requirements-docs.txt
.\scripts\generate-portfolio-docs.ps1
python .\scripts\validate-artifacts.py
```

## Repository layout

```text
.
|-- labs/
|   |-- lab1/                 # Mountain Hiking source, data and tests
|   |-- lab2/                 # Football Club & Player source, data and tests
|   `-- lab3/                 # Employee Payroll source, data and tests
|-- docs/
|   |-- ai-audit-logs/        # AI-use records with evidence indexes
|   |-- diagrams/             # UML and algorithm flow diagrams
|   `-- editable/             # Editable report sources
|-- evidence/                 # Verification and artifact integrity records
|-- output/pdf/               # Final report PDFs
`-- scripts/                  # Reproducible verification/document tooling
```

## Integrity note

This repository documents coursework and engineering evidence; it does not claim grades, endorsements, or work that cannot be verified from the included artifacts. Reuse must follow the [MIT License](LICENSE) and the academic-integrity rules of the reader's institution.
