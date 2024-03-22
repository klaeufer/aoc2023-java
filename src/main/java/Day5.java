import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.*;

public class Day5 {

  public static void main(String[] args) throws Throwable {
    final var input = Files
        .newBufferedReader(Paths.get("data/day5input.txt"))
        .lines()
        .iterator();

    final var result = process(input);

    System.out.println(result.part1); // 174137457
    System.out.println(result.part2); // 1493866
  }

  private static final Pattern number = Pattern.compile("(\\d+)");

  static List<Long> getNumbersFromLine(final String line) {
    final var matcher = number.matcher(line);
    return matcher
        .results()
        .map(MatchResult::group)
        .map(Long::parseLong)
        .toList();
  }

  static List<Long> makeSeq(final Iterator<String> input) {
    final var line = input.next();
    final var result = getNumbersFromLine(line);
    input.next(); // skip blank line
    return result;
  }

  record Triple(long base, long start, long length) {}

  static Optional<Function<Long, Long>> makeMap(final Iterator<String> input) {
    if (!input.hasNext()) return Optional.empty();

    input.next(); // skip section header

    final var stream = StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(input, Spliterator.ORDERED),
        false);

    final var ranges = stream
        .takeWhile(line -> !line.isEmpty())
        .map(line -> {
          final var nums = getNumbersFromLine(line);
          return new Triple(nums.get(0), nums.get(1), nums.get(2));
        })
        .toList();

    return Optional.of(i ->
        ranges
            .stream()
            .filter(r -> r.start <= i && i < r.start + r.length)
            .findFirst()
            .map(r -> r.base + i - r.start)
            .orElse(i)
    );
  }

  record Result(long part1, long part2) {}

  static Result process(final Iterator<String> input) {
    final var seeds = makeSeq(input);
    final var allMaps = Stream
        .generate(() -> makeMap(input))
        .takeWhile(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList()); // DO NOT replace with a simple toList()
    Collections.reverse(allMaps);      // for reverse to work!
    final var seedToLocation = allMaps
        .stream()
        .reduce((f, g) -> s -> f.apply(g.apply(s)))
        .get();

    final var part1 = seeds
        .stream()
        .map(seedToLocation::apply)
        .min(Long::compare)
        .get();
    System.err.println(STR."part1: \{part1}");

    //    https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherers.html
    final var part2 = seeds
        .stream()
        .gather(Gatherers.windowFixed(2))
        .map(p -> {
          final var head = p.getFirst();
          return LongStream
              .range(head, head + p.getLast())
              .map(seedToLocation::apply)
              .min()
              .getAsLong();
        })
        .min(Long::compare)
        .get();

      System.err.println(STR."part2: \{part2}");
      return new Result(part1, part2);
  }
}
