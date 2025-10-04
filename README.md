"# 🏎️ Car Racing Bet - Android Game

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An exciting **Android racing betting game** where players choose their champion car, place bets, and watch thrilling races unfold! Built with modern Android development practices and featuring immersive audio, smooth animations, and comprehensive game statistics.

## 🎮 Game Features

### Core Gameplay
- **🏁 5 Unique Racing Cars**: Each with distinct stats and winning odds
- **💰 Multi-Betting System**: Bet on multiple cars simultaneously with individual amounts
- **⏸️ Race Controls**: Pause and skip functionality for better user experience
- **🎲 Fair Racing Algorithm**: Advanced physics with randomized outcomes
- **⚡ Real-time Racing**: Watch cars compete with dynamic animations
- **🔊 Immersive Audio**: ToneGenerator-based sound effects and engine sounds

### Game Mechanics
- **🏆 Achievement System**: 15+ achievements to unlock
- **📊 Statistics Tracking**: Detailed race history and performance analytics
- **💎 Coin Management**: Earn/lose coins based on race outcomes
- **🎯 Multi-Betting Strategy**: Bet on multiple cars with individual amounts and odds
- **🎮 Race Control**: Pause races or skip to results instantly
- **⚙️ Settings Panel**: Audio controls and game reset functionality

### User Interface
- **🌟 Modern Material Design**: Clean, intuitive interface
- **📱 Responsive Layout**: Optimized for various screen sizes
- **🎨 Smooth Animations**: Car movements, countdown timers, and transitions
- **🔄 Interactive Elements**: Touch-friendly buttons with visual feedback
- **⏯️ Race Controls**: Intuitive pause/resume and skip buttons during races

## 🚀 Installation

### Prerequisites
- **Android Studio** 4.2 or higher
- **Android SDK** API level 21 (Android 5.0) or higher
- **Java Development Kit (JDK)** 8 or higher

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/Zun1702/CarRacingF4.git

# Navigate to project directory
cd CarRacingF4

# Build the project
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug
```

### APK Installation
Download the latest APK from the [Releases](https://github.com/Zun1702/CarRacingF4/releases) section and install on your Android device.

## 🏗️ Architecture

### Project Structure
```
app/
├── src/main/java/com/example/carracing/
│   ├── MainActivity.java              # Home screen & navigation
│   ├── BettingActivity.java           # Car selection & betting
│   ├── RacingActivity.java            # Race simulation & results
│   ├── AchievementsActivity.java      # Achievement display
│   ├── HistoryActivity.java           # Statistics & race history
│   ├── managers/
│   │   ├── GameAudioManager.java      # Audio system
│   │   ├── AchievementManager.java    # Achievement tracking
│   │   └── RaceHistoryManager.java    # Data persistence
│   ├── models/
│   │   ├── Car.java                   # Car entity with racing physics
│   │   └── Achievement.java           # Achievement data model
│   └── utils/
│       └── ErrorHandler.java          # Error handling & logging
└── src/main/res/
    ├── layout/                        # XML layouts
    ├── drawable/                      # Graphics & animations
    ├── values/                        # Colors, strings, styles
    └── raw/                          # Audio assets
```

### Key Components

#### 🎯 Racing Algorithm
- **Advanced Physics Engine**: Fatigue, slipstream, and momentum effects
- **Fair Competition**: Individual random generators per car
- **Dynamic Conditions**: Real-time track condition variations
- **Collision Detection**: Realistic car interactions during races

#### 🔊 Audio System
- **ToneGenerator Integration**: Dynamic sound generation
- **Engine Sounds**: Intensity-based audio feedback
- **UI Sound Effects**: Button clicks, countdown, victory sounds
- **Volume Controls**: User-configurable audio settings

#### 💾 Data Management
- **SharedPreferences**: Persistent game state storage
- **JSON Serialization**: Complex data structure handling
- **Race History**: Comprehensive game statistics tracking
- **Achievement Progress**: Real-time achievement monitoring

## 🎲 Game Mechanics

### Racing Cars
| Car | Speed | Odds | Characteristics |
|-----|-------|------|----------------|
| 🔴 Red Thunder | 82 | 2.1x | Balanced performance |
| 🔵 Blue Lightning | 84 | 2.0x | High speed specialist |
| 🟢 Green Machine | 81 | 2.2x | Consistent performer |
| 🟡 Yellow Bullet | 83 | 2.0x | All-around competitor |
| 🟣 Purple Phantom | 80 | 2.3x | High risk, high reward |

### Betting System
- **Minimum Bet**: 10 coins
- **Maximum Bet**: Your entire balance
- **Payout**: Bet amount × Car odds (if your car wins)
- **Starting Balance**: 1,000 coins

### Achievement Categories
- **🏆 Victory Achievements**: First Win, Lucky Streak, Consistent Winner
- **💰 Financial Achievements**: Big Winner, High Roller, Millionaire
- **🎯 Gameplay Achievements**: Risk Taker, Speed Demon, Perfect Predictor

## 🛠️ Development

### Built With
- **Android SDK**: Native Android development
- **Java**: Primary programming language
- **Material Design Components**: Modern UI framework
- **ConstraintLayout**: Flexible UI layouts
- **RecyclerView**: Efficient list displays
- **ToneGenerator**: Dynamic audio generation

### Key Features Implementation
- **🎮 Game State Management**: Comprehensive state persistence
- **🎨 Custom Animations**: ObjectAnimator for smooth transitions
- **🔄 Real-time Updates**: Handler-based racing simulation
- **📊 Data Visualization**: Statistics charts and progress tracking
- **⚙️ Settings Management**: User preferences and game reset

### Code Quality
- **Error Handling**: Comprehensive exception management
- **Logging System**: Debug and production logging
- **Input Validation**: User input sanitization
- **Memory Management**: Efficient resource handling

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### How to Contribute
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Android Developer Community** for excellent documentation
- **Material Design** for beautiful UI components
- **Open Source Contributors** for inspiration and best practices
- **Beta Testers** for valuable feedback and bug reports

---

<div align="center">

**⭐ Star this repository if you found it helpful! ⭐**

Made with ❤️ by [Zun1702](https://github.com/Zun1702)

</div>
" 
