## Version 1.1.0

### Bug Fixes
* **Lava Occlusion Fix:** Fixed a critical rendering bug where the health bar would disappear or become obscured when a mob was standing near or in front of lava. The health bar now correctly writes to the depth buffer.
* **Fluid Transparency Hole Fix:** Resolved a visual glitch where the invisible bounding box around the health numbers would "punch a hole" through lava and water behind it.
* **Underwater Legibility:** The health bar and text now render at 100% full brightness independent of the environment. This ensures it remains perfectly legible even in pitch-black environments, such as deep underwater or in unlit caves.
