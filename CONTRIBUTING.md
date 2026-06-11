# Contributing to Build Status Lite

Source code is hosted on [GitLab](https://gitlab.com).
New feature proposals and bug fix proposals should be submitted as merge requests.

Before submitting a change, please add tests that verify it.

## Code formatting

Formatting is maintained by the `spotless` Maven plugin. Before submitting a merge request, confirm formatting is correct:

```
mvn spotless:apply
```

## Code Coverage

```
mvn -P enable-jacoco clean install jacoco:report
```

Open the report:
- Windows: `start target\site\jacoco\index.html`
- Linux: `xdg-open target/site/jacoco/index.html`

## Static analysis

```
mvn spotbugs:check   # check for issues
mvn spotbugs:gui     # review in GUI
```

## Running tests

```
mvn clean -DforkCount=1C verify
```

## Report an Issue

Open an issue in the project repository.
