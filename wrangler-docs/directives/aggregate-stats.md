# Aggregate Stats

Aggregates byte sizes and time durations from columns, providing total or average values in specified output units.

## Usage

```sql
aggregate-stats <source-size-column> <source-time-column> <target-size-column> <target-time-column> [output-size-unit] [output-time-unit] [aggregation-type]
```

## Arguments

- `<source-size-column>`: The name of the column containing byte size values to aggregate.
- `<source-time-column>`: The name of the column containing time duration values to aggregate.
- `<target-size-column>`: The name of the column where the aggregated size will be stored.
- `<target-time-column>`: The name of the column where the aggregated time will be stored.
- `[output-size-unit]`: (Optional, default: "MB") The unit for the output size value.
- `[output-time-unit]`: (Optional, default: "s") The unit for the output time value.
- `[aggregation-type]`: (Optional, default: "total") The type of aggregation to perform. Must be either "total" or "average".

## Example

```sql
aggregate-stats size_column duration_column total_size total_duration MB s total
```

This example will:
1. Aggregate all values from `size_column` and `duration_column`
2. Store the total size in `total_size` (in MB)
3. Store the total duration in `total_duration` (in seconds)

## Supported Output Units

### Size Units
- B (bytes)
- KB (kilobytes)
- MB (megabytes)
- GB (gigabytes)
- TB (terabytes)

### Time Units
- ns (nanoseconds)
- us (microseconds)
- ms (milliseconds)
- s (seconds)
- m (minutes)
- h (hours)
- d (days)

## Notes

- The directive is case-insensitive for unit suffixes.
- If `aggregation-type` is "average", the aggregated values will be divided by the number of rows.
- The input columns must contain values parsed using `parse-byte-size` and `parse-time-duration` directives.
- The output values are always floating-point numbers to maintain precision.

## Supported Output Units

### Size Units
- B (bytes)
- KB (kilobytes)
- MB (megabytes)
- GB (gigabytes)
- TB (terabytes)

### Time Units
- ns (nanoseconds)
- us (microseconds)
- ms (milliseconds)
- s (seconds)
- m (minutes)
- h (hours)
- d (days)

## Notes

- The directive is case-insensitive for unit suffixes.
- If `aggregation-type` is "average", the aggregated values will be divided by the number of rows.
- The input columns must contain values parsed using `parse-byte-size` and `parse-time-duration` directives.
