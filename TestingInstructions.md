# 🧪 Testing Instructions - Car Racing Game

## 📱 **BUILD & RUN STEPS:**

### 1. **Build Project**
**Option A: Android Studio (RECOMMENDED)**
```
1. Open project in Android Studio
2. Click "Build" → "Make Project" (Ctrl+F9)
3. Wait for build to complete
4. Click "Run" → "Run 'app'" (Shift+F10)
```

**Option B: Command Line (if Java 11+ available)**
```bash
cd "d:\Learning\PRM392\CarRacing"
.\gradlew assembleDebug
.\gradlew installDebug
```

### 2. **Expected Build Output**
- ✅ No compilation errors
- ✅ All resources linked successfully
- ✅ APK generated successfully

---

## 🎮 **FUNCTIONALITY TESTING:**

### **Test Case 1: MainActivity Flow**
1. **Launch app**
   - ✅ Game title displays: "🏁 CAR RACING BET"
   - ✅ Car demo animations working
   - ✅ Welcome message visible

2. **Player Name Input**
   - ✅ Enter name (min 2 characters)
   - ✅ Validation: Empty name shows error toast
   - ✅ Name saved to SharedPreferences
   - ✅ "START GAME" button navigates to BettingActivity

### **Test Case 2: BettingActivity Flow**
1. **UI Display**
   - ✅ Player name shown: "Welcome, [PlayerName]!"
   - ✅ Balance displays: "Balance: 1000 coins"
   - ✅ 5 cars visible in RecyclerView
   - ✅ Each car shows: Name, Speed, Odds

2. **Car Selection**
   - ✅ Tap car → Selection indicator appears
   - ✅ Card elevation increases for selected car
   - ✅ Only one car selectable at a time

3. **Betting Logic**
   - ✅ Enter bet amount (10-1000 range)
   - ✅ Potential winnings calculate: `bet * odds`
   - ✅ Validation: Empty bet shows error
   - ✅ Validation: Insufficient funds shows error
   - ✅ "START RACE" navigates to RacingActivity

### **Test Case 3: RacingActivity Flow**
1. **Race Setup**
   - ✅ Race info shows: "Your Bet: X coins on CarName"
   - ✅ 5 race lanes visible
   - ✅ Cars positioned at start line
   - ✅ Progress bars at 0%

2. **Countdown Sequence**
   - ✅ Shows "3" → "2" → "1" → "GO!"
   - ✅ Countdown text animates (scale effect)
   - ✅ Race starts after "GO!"

3. **Race Animation**
   - ✅ Cars move across screen (random speeds)
   - ✅ Progress bars update in real-time
   - ✅ Car positions track correctly
   - ✅ Position display updates: "1. CarName", etc.
   - ✅ Selected car highlighted with ★
   - ✅ Race finishes when car reaches end

4. **Race Completion**
   - ✅ Winner car gets scale animation
   - ✅ Auto-navigate to ResultActivity after 2s

### **Test Case 4: ResultActivity Flow**
1. **Result Display**
   - ✅ Win scenario: "🎉 CONGRATULATIONS! 🎉"
   - ✅ Lose scenario: "😢 BETTER LUCK NEXT TIME"
   - ✅ Winner announcement: "🏆 Winner: CarName"
   - ✅ Bet result: Win amount or loss amount

2. **Balance Update**
   - ✅ New balance calculated correctly
   - ✅ Win: `oldBalance - bet + winnings`  
   - ✅ Lose: `oldBalance - bet`
   - ✅ Balance saved to SharedPreferences

3. **Navigation Options**
   - ✅ "PLAY AGAIN" → BettingActivity (if balance ≥ 10)
   - ✅ "MAIN MENU" → MainActivity
   - ✅ Insufficient funds → Reset balance dialog

4. **Animations**
   - ✅ Result icon bounces in
   - ✅ Text fades in
   - ✅ Buttons slide up from bottom

---

## 🐛 **COMMON ISSUES & SOLUTIONS:**

### **Build Issues:**
- **Java Version Error:** Build in Android Studio instead of command line
- **Resource Linking:** Vector drawables fixed with path elements
- **Missing Dependencies:** Already added to build.gradle.kts

### **runtime Issues:**
- **App Crashes on Launch:** Check AndroidManifest.xml activities
- **Car Images Not Showing:** Fallback to simple car drawables available
- **SharedPreferences Errors:** Clear app data and restart

### **UI Issues:**
- **Layout Overlapping:** All layouts use ConstraintLayout for proper positioning
- **Colors Not Showing:** All colors defined in colors.xml
- **Animations Jerky:** Handler-based animations should be smooth

---

## 📊 **EXPECTED PERFORMANCE:**

### **Core Functionality:**
- ✅ App launches without crashes
- ✅ All 4 activities navigate correctly
- ✅ Data persists between sessions
- ✅ Race animations run smoothly (60fps target)
- ✅ Bet calculations accurate
- ✅ Balance management works properly

### **User Experience:**
- ✅ Intuitive navigation flow
- ✅ Clear visual feedback for selections
- ✅ Smooth transitions between screens
- ✅ Proper error handling and validation
- ✅ Responsive UI on different screen sizes

---

## 🎯 **SUCCESS CRITERIA:**

**MINIMUM VIABLE PRODUCT:**
- [x] Complete game flow: Main → Betting → Racing → Result
- [x] Player name persistence
- [x] Car selection and betting system
- [x] Race animation with winner determination
- [x] Win/loss calculation and balance updates
- [x] Replay functionality

**ENHANCED FEATURES (Future):**
- [ ] Audio system (background music + sound effects)
- [ ] Particle effects during racing
- [ ] Achievement system
- [ ] Race history tracking
- [ ] Multiple betting types
- [ ] Car upgrade system

---

## 🚀 **READY FOR DEMO!**

Game implements all core features dari original requirements:
1. ✅ Tên người chơi input & save
2. ✅ Danh sách 5 xe đua với odds
3. ✅ Betting system với validation
4. ✅ Race animation với countdown
5. ✅ Win/lose determination
6. ✅ Balance management
7. ✅ Play again functionality

**🎮 GAME IS READY TO PLAY! 🏁**