# Release Notes

## v1.0 - Initial Release

First official release of the Drawing Tool application.

### Features:
- Basic drawing functionality with free-hand pencil tool
- Four color options (Black, Red, Blue, Green)
- Adjustable brush size with slider control
- Canvas clearing function
- Intuitive user interface with Swing components

### Technical Notes:
- Built with Java Swing
- Compatible with Java 8+
- Single-file implementation for easy deployment

## v1.1 - Save Image Feature

This update adds the ability to save your drawings.

### New Features:
- Save functionality to export drawings as PNG files
- File selection dialog for choosing save location
- Automatic file extension handling

### Improvements:
- Improved UI with save button
- Better error handling for save operations

### Technical Notes:
- Added BufferedImage support for image saving
- Implemented Java ImageIO for file operations

## v1.2 - Shape Drawing Tools

This update adds shape drawing capabilities to the application.

### New Features:
- Shape drawing tools (Rectangle, Oval, Line)
- Option to fill shapes with color
- Tool selection panel with toggle buttons

### Improvements:
- Restructured control panel for better organization
- Clearer visual feedback during drawing
- Fixed minor rendering issues

### Technical Notes:
- Implemented proper event handling for shape tools
- Improved code structure with Shape class
- Enhanced drawing panel to support multiple drawing modes
