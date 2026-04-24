# Service Territory Search Console

## Overview

Service Territory Search Console is a Java-based internal lookup utility for reviewing U.S. state coverage, searching territory entries, and filtering operational regions.

This project is positioned as a recruiter-ready Java console portfolio piece. It upgrades a generic state-search exercise into a more practical internal operations tool that better matches sales support, logistics planning, territory coverage lookup, and service region review workflows.

## Real-World Business Use Case

This project maps to practical workflows used by:

- Sales Operations Teams
- Logistics And Service Coverage Teams
- Territory Planning Workflows
- Internal Lookup Utilities
- Java Console Application Development

A team may need to answer questions such as:

- Is a state part of the covered service territory?
- Which states belong to a specific region?
- How can a team search for a state quickly by name or code?
- How can a simple console utility present territory coverage more clearly?

This tool is useful for territory lookup, internal reference workflows, and portfolio presentation of a small Java operations utility.

## Key Features

- Full Covered State Listing
- Search By State Name
- Search By State Code
- Region Filtering
- Coverage Summary Reporting
- Clean Console Workflow
- Maven Project Structure

## Tech Stack

- Java 17
- Maven

## Repository Contents

- `pom.xml`
- `src/main/java/com/territory/Main.java`
- `src/main/java/com/territory/TerritoryState.java`
- `src/main/java/com/territory/TerritorySearchService.java`
- `README.md`

## How To Run

### Build

```powershell
mvn clean package
