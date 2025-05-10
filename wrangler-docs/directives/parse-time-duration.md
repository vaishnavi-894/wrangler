# Parse Time Duration

Parses time duration values with units (e.g., "150ms", "2s") and converts them to a canonical form.

## Usage

```sql
parse-time-duration <column-name>
```

## Arguments

- `<column-name>`: The name of the column containing time duration values to parse.

## Examples

```sql
# Parse a column containing various time duration formats
parse-time-duration duration_column
```

If `duration_column` contains values like:
- "150ms"
- "2s"
- "5m"
- "1.5h"
- "2d"

The directive will parse these values and convert them to a canonical form (nanoseconds) that can be used in calculations or comparisons.

## Supported Units

### Short Form Units
- ns (nanoseconds)
- us (microseconds)
- ms (milliseconds)
- s (seconds)
- m (minutes)
- h (hours)
- d (days)

### Long Form Units
- nanoseconds
- microseconds
- milliseconds
- seconds
- minutes
- hours
- days

## Notes

- The directive is case-insensitive for unit suffixes.
- It supports both short and long forms of unit suffixes (e.g., "ms" and "milliseconds" are both valid).
- If no unit is specified, the value is assumed to be in seconds.
- The output is always in nanoseconds for consistency with other operations.
- The directive can handle decimal values (e.g., "1.5h").
- The directive will throw an error if an unsupported unit is used or if the value cannot be parsed.
- Negative durations are supported (e.g., "-1h").
