# Build Status Lite Plugin

A lightweight Jenkins plugin that exposes a single unauthenticated endpoint for embedding build status badges.

Forked from [embeddable-build-status](https://github.com/jenkinsci/embeddable-build-status-plugin) with the following changes:
- Only the unprotected (public) badge endpoint is kept
- Multibranch Pipeline branches with slashes in their names work without double URL-encoding
- No Pipeline DSL, no per-build selectors, no protected badge URLs

## Badge URL

```
https://<jenkinsroot>/buildStatusLite/icon?job=<jobPath>[&style=...][&subject=...][&status=...]
```

The endpoint requires `ViewBuildStatusLite` permission on the job (defaults to `Read`).

A UI for generating the URL is available in the job sidebar under **Build Status Lite**.

## Query Parameters

### `job`

Full path to the job. For **Multibranch Pipelines**, slashes in branch names can be passed as-is — no double encoding required.

```
?job=my-pipeline/feature/my-branch   ✔
```

Any selector implemented via `JobSelectorExtensionPoint` is also accepted.

If omitted, an untethered badge is returned (useful for testing styles).

### `style`

Four badge styles are supported:

| Style | Example |
|---|---|
| `flat` (default) | ![flat](src/doc/flat_unconfigured.svg) |
| `flat-square` | ![flat-square](src/doc/flat-square_unconfigured.svg) |
| `plastic` | ![plastic](src/doc/plastic_unconfigured.svg) |
| `ball-<size>` | standard Jenkins balls (`ball-16x16`, `ball-32x32`, …) |

> When using `ball-<size>`, all other parameters have no effect.

### `subject` and `status`

Override the badge label and value text.

```
?subject=Tests&status=passing
```

Supports variable substitution — see [Parameter Resolver](#parameter-resolver) below.

## Parameter Resolver

`subject` and `status` support `${variable}` placeholders resolved from the latest build.

Built-in variables:

| Variable | Description |
|---|---|
| `buildId` | Build ID string |
| `buildNumber` | Build number |
| `displayName` | Build display name |
| `description` | Build description |
| `duration` | Build duration string |
| `startTime` | Build start time |
| `params.<Name>` | Build parameter value |
| `params.<Name>\|<Default>` | Build parameter with fallback |

Example:
```
?subject=Build ${params.BRANCH|main}&status=${displayName}
```

## Extension Points

### `JobSelectorExtensionPoint`

Implement to provide custom job lookup logic. The `job` query parameter is passed to each registered selector before falling back to the default path-based lookup.

```java
@Extension
public class MyJobSelector implements JobSelectorExtensionPoint {
    @Override
    public Job select(String selector) {
        // return a Job or null
    }
}
```

### `ParameterResolverExtensionPoint`

Implement to provide custom `${variable}` resolution.

```java
@Extension
public class MyResolver implements ParameterResolverExtensionPoint {
    @Override
    public String resolve(Actionable actionable, String parameter) {
        // return resolved string or the original parameter if not handled
    }
}
```
