# bcrypt Benchmark

This tool is a very small helper tool to determine the best round count for bcrypt password hashing.

## Compile Tool

Run `./gradlew clean jar` to build the executable jar file. You can find it in `build/libs/`.

## Parameters

You can pass one string that will be used for the log file name and the title of the benchmark. Otherwise, a default name will be used.

## Configuration

If you need, you can modify the default configuration before compilation in the sourcecode. Available configuration items:

- `ITERATIONS_FOR_AVERAGE`: Sets the number of measurements that get used to calculate one average for a round setting. Default: `10`
- `MINIMUM_ROUNDS`: The default number of salting rounds for bcrypt. Default: `9`
- `MINIMUM_HASHING_TIME_NS`: Sets the time it should take in the best case for one hashing step in nanoseconds. Default: `2.5 x 10^8 ns`

## Execution

Run the tool with `java -jar <path-to-jar>/bcrypt-Benchmark-<version>.jar`. After some seconds, a text file will be generated in the same directory as the tool.

The log file contains all measurements taken. At the end the tool recommends a round setting for your hardware configuration.
