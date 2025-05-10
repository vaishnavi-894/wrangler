# Parse Byte Size

Parses byte size values with units (e.g., "10KB", "5MB") and converts them to a canonical form.

## Usage

```sql
parse-byte-size <column-name>
```

## Arguments

- `<column-name>`: The name of the column containing byte size values to parse.

## Examples

```sql
# Parse a column containing various byte size formats
parse-byte-size size_column
```

If `size_column` contains values like:
- "10KB"
- "5MB"
- "2GB"
- "1.5TB"

The directive will parse these values and convert them to a canonical form (bytes) that can be used in calculations or comparisons.

## Supported Units

### Binary Units (1024-based)
- B (bytes)
- KB (kilobytes) = 1024 B
- MB (megabytes) = 1024 KB
- GB (gigabytes) = 1024 MB
- TB (terabytes) = 1024 GB

### Decimal Units (1000-based)
- B (bytes)
- kB (kilobytes) = 1000 B
- MB (megabytes) = 1000 kB
- GB (gigabytes) = 1000 MB
- TB (terabytes) = 1000 GB

## Notes

- The directive is case-insensitive for unit suffixes.
- It supports both decimal (1000-based) and binary (1024-based) prefixes.
- If no unit is specified, the value is assumed to be in bytes.
- The output is always in bytes for consistency with other operations.
- The directive can handle decimal values (e.g., "1.5MB").
- The directive will throw an error if an unsupported unit is used or if the value cannot be parsed.
