## Detection Principle

The fundamental principle behind Anti-AJR is that performing a jump for exactly one game tick is exceptionally difficult to achieve consistently through manual input.

At 20 TPS, one game tick is approximately 50 milliseconds. A player must press and release their jump key within this narrow window to produce a one-tick jump.

While this is theoretically possible through legitimate input, performing more than three one-tick jumps within a window of ten jumps would be extremely difficult for a human player to achieve.

This applies to jumps performed during combat hit windows as well as ordinary jumps. Anti-AJR does not exclusively analyse normal movement; jumps occurring within hit windows are also relevant to its detection.

### Current Detection

Currently, Anti-AJR monitors jumps and checks the number of one-tick jumps within a rolling window of ten samples, including jumps occurring during hit windows.

If more than three of those ten jumps are completed within a single tick, the player is flagged as suspected of using automated jumping.

This is the initial detection method. More advanced consistency analysis and additional checks are planned but are not yet implemented.

### Future Development

See [`plan.txt`](plan.txt) for planned improvements, including:
- Additional measures to reduce false positives.
- More advanced jump behaviour analysis.
- Countermeasures against potential bypasses.
- Further improvements to detection reliability.
