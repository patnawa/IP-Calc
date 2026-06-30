# Changelog

All notable changes to the IP Calculator Suite project will be documented in this file.

## [2.0.0] - 2026-06-30
### Added
- **Subnetting Quiz**: Interactive training mode for CCNA and Network+ exams with scoreboards, streak trackers, and explanations.
- **Visual IP Map**: Bar graph visualizer in VLSM displaying allocated subnets and remaining free address space.
- **IPv6 EUI-64 Generator**: Converts MAC address and network prefix to EUI-64 address with step-by-step logic.
- **MAC Vendor Lookup**: Resolves manufacturer details and formats MAC notation conversions.
- **CIDR Prefix Chart**: Cheat sheet listing all prefix ranges (/0 to /32) with hosts and wildcard details.
- **TCP/UDP Common Ports**: Searchable and filterable reference of well-known network ports.
- **IP Subnet Containment Checker**: Verify if a target IP falls inside a subnet range.
- **Subnet Comparison**: Side-by-side comparative table of two subnets detecting conflicts and containment.
- **Modern Scrollable Navigation**: Top scrollable tab bar accommodating 12 features with Crossfade animations.
- **Bright Cute Icon**: New bright, modern, and cute app icon featuring a robot networking character.

### Fixed
- **IPv6 Compression**: Corrected leading/trailing zero runs :: expansion and compression logic in `IPCalculator.kt`.
- **IPv6 Validation**: Fixed double-colon count threshold allowing multiple :: sequence errors.
- **VLSM Overflow**: Enlarged VLSM power-of-two base size calculation from `Int` to `Long` to prevent memory overflow.
- **Route Summarization**: Normalizes inputs to network address ranges first before computing summaries.
- **Rotation States**: Wrapped all input and calculation fields inside `rememberSaveable` to avoid data loss on screen rotation.
- **Deprecated APIs**: Cleaned up deprecated `LocalClipboardManager` and `centerAlignedTopAppBarColors` usages.

## [1.0.0] - 2026-06-28
### Added
- **Subnet Calculator**: IPv4 & IPv6 network parameter computation.
- **Binary View**: Colored and formatted binary grid representation of subnet masks and IP octets.
- **VLSM / FLSM Screen**: Dynamic host requirements input with interactive result cards.
- **IP Converter**: 4-way translation between dot-decimal, binary, hex, and decimal long values.
- **Route Summarizer**: Combining multiple network blocks into a single summary network.
- **Modern M3 UI**: Tabbed interface using Material 3 themes and dynamic color palettes.
- **Launcher Icons**: Sleek glassmorphism icon compiled for all standard screen resolutions.
