# Changelog

All notable changes to the IP Calculator Suite project will be documented in this file.

## [2.2.1] - 2026-07-09
### Fixed
- **OUI Registry**: Replaced invalid hex entry `0024H4` (Samsung) with correct `0024A4` — previously never matched.
- **Quiz Generator**: Restricted prefix range to /8–/29 to prevent duplicate/ambiguous answer options at /30, /31, and /32 prefixes.
- **Binary Converter**: Enforced strict dotted-octet format (8 bits per octet × 4) in `binaryToIPv4()`; previously accepted arbitrarily dotted input.
- **Supernetting "Add Subnet"**: Prevented invalid IP generation when the third octet overflowed past 255; now wraps octets safely.
- **Scanner → WOL Navigation**: Scanner sweep now pre-fills the broadcast IP on the WOL screen instead of doing nothing with it.
- **Theme Persistence**: User's light/dark theme preference is now saved to SharedPreferences and restored on next app launch.
- **History/Favorites Storage**: Replaced comma delimiter with ASCII Unit Separator (`\u001F`) to prevent corruption from entries containing commas.
- **Test Suite**: Replaced non-compiling scaffold test with a comprehensive `IPCalculatorTest` covering IPv4/IPv6 calculation, VLSM/FLSM, supernetting, quiz generation, MAC formatting, and more.

## [2.2.0] - 2026-07-02
### Added
- **Scan History & Favorites**: Integrated persistent saving of target pings and DNS domains with quick-clickable suggestion chips and star toggles.
- **PDF/Image Exporter**: Rendered subnetting allocations directly to high-quality sharing-ready PNG graphics with a native FileProvider share chooser.
- **Wake-on-LAN (WOL)**: Broadcast UDP magic packets to wake remote host devices. Linked online sweep results directly to the WOL trigger interface.
- **Interactive Subnet Map**: Implemented high-performance pan/zoom gestures on an allocation map Canvas representing network sizing blocks.
- **Quick Calculator Overlay**: Draggable picture-in-picture floating Compose overlay window with dynamic keyboard focus management.

## [2.1.0] - 2026-06-30
### Added
- **Searchable Dashboard Grid**: Replaced the horizontal tabs navigation with a modern categorized launcher dashboard with dynamic search filtering.
- **Cisco ACL Generator**: Generates wildcard masks and Cisco IOS ACL permit/deny command snippets dynamically.
- **Ping & Subnet Sweep Scanner**: Run single ICMP/TCP pings with latency logs, or scan/sweep an entire subnet range concurrently using coroutines to detect online hosts.
- **DNS Resolver & WHOIS Query**: Lookup DNS records (A, AAAA, MX, NS, CNAME, TXT) via Google DoH, and WHOIS registration info via RDAP endpoints.
- **Network Design Wizard**: Interactive subnet planner using pre-configured layout templates (Corporate Office, Startup, Smart Home).
- **Interactive Cheat Sheets**: Study guides for the 7-layer OSI Model, T568A/T568B RJ-45 Ethernet wiring color codes, IPv4/IPv6 packet headers, and TIA-598 fiber codes.
- **Expanded Translations**: Broadened localization database to support 14 global languages including Japanese (JA) and Korean (KO).

## [2.0.0] - 2026-06-30
### Added
- **Global Multi-Language Switcher**: Integrated a dynamic translation engine supporting English, Thai, Spanish, Chinese, Japanese, German, and French, with an instant top app bar dropdown toggler.
- **Subnetting Quiz**: Interactive training mode for CCNA and Network+ exams with scoreboards, streak trackers, and explanations.
- **Visual IP Map**: Bar graph visualizer in VLSM displaying allocated subnets and remaining free address space.
- **IPv6 EUI-64 Generator**: Converts MAC address and network prefix to EUI-64 address with step-by-step logic.
- **MAC Vendor Lookup**: Resolves manufacturer details and formats MAC notation conversions.
- **CIDR Prefix Chart**: Cheat sheet listing all prefix ranges (/0 to /32) with hosts and wildcard details.
- **TCP/UDP Common Ports**: Searchable and filterable reference of well-known network ports.
- **IP Subnet Containment Checker**: Verify if a target IP falls inside a subnet range.
- **Subnet Comparison**: Side-by-side comparative table of two subnets detecting conflicts and containment.
- **Modern Scrollable Navigation**: Top scrollable tab bar accommodating 12 features with Crossfade animations.
- **Professional Tech Icon**: New professional, sleek, and modern app icon featuring a glowing 3D network node grid.

### Fixed
- **Screen Padding**: Wrapped screen layouts inside Scaffold padding Box to prevent the top navigation bar from overlapping or blocking screen content.
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
