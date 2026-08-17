# Domain Glossary

This document captures the Ubiquitous Language for the domain model.

## Value Objects

### Email
An electronic mail address used for communication and identification. It is normalized (trimmed and lowercased) and validated against standard email format constraints.

### Phone
A contact telephone number conforming to E.164-compatible international numbering standards (7 to 15 digits, with an optional leading `+`). Formatted input is normalized by removing separating characters such as spaces, hyphens, and parentheses.

### Name
A person's given/first name. Normalized and validated to contain valid Unicode letters, spaces, hyphens, or apostrophes within length constraints (1 to 100 characters).

### Surname
A person's family/last name. Normalized and validated to contain valid Unicode letters, spaces, hyphens, or apostrophes within length constraints (1 to 100 characters).

### FullName
A composite value object combining a `Name` and a `Surname` to represent a person's complete name.
