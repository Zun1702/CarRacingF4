# 🚗💨 FIXED: Khói và Z-Order Layout

## ✅ **ĐÃ FIX:**

### 1. **Thêm khói exhaust hiển thị**
- ✅ **Layout**: Thêm `ivSmoke1` trong race lane 1
- ✅ **Animation**: Start `exhaust_smoke_animated.xml` animation
- ✅ **Movement**: Smoke follow car movement với offset -25px
- ✅ **Styling**: Alpha 0.7, scale 0.8 để natural hơn

### 2. **Fix Z-Order layout** 
- ✅ **Changed**: `LinearLayout` → `RelativeLayout` 
- ✅ **Layer Order**: 
  1. **ProgressBar** (đằng sau, `android:layout_centerVertical`)
  2. **Smoke** (giữa, behind car)
  3. **Car** (đằng trước, on top)
  4. **Flag** (cuối đường đua)

## 🎯 **CHI TIẾT KỸ THUẬT:**

### **Layout Structure (Race Lane 1):**
```xml
<RelativeLayout>
  <!-- 1. Progress bar (background layer) -->
  <ProgressBar android:layout_centerVertical />
  
  <!-- 2. Exhaust smoke (middle layer) -->
  <ImageView android:id="ivSmoke1" 
             android:src="exhaust_smoke_animated" />
  
  <!-- 3. Car (foreground layer) -->
  <ImageView android:id="ivCar1" 
             android:src="racingcar_red" />
  
  <!-- 4. Finish flag -->
  <TextView android:text="🏁" />
</RelativeLayout>
```

### **RacingActivity Updates:**
```java
// 1. Added smoke ImageView array
private ImageView[] smokeImageViews;

// 2. Initialize smoke views  
smokeImageViews[0] = findViewById(R.id.ivSmoke1);

// 3. Start smoke animations
private void startSmokeAnimations() {
    if (smokeDrawable instanceof Animatable) {
        ((Animatable) smokeDrawable).start();
    }
}

// 4. Smoke follows car movement
private void animateCarMovement() {
    smokeImageViews[i].setTranslationX(carTranslationX - 25);
}
```

## 🏎️ **KẾT QUẢ:**

### **Visual Improvements:**
- ✅ **Khói exhaust** hiển thị và animation liên tục
- ✅ **Progress bar** không còn đè lên xe
- ✅ **Xe chạy trên** progress bar 
- ✅ **Smoke theo** xe di chuyển
- ✅ **Layering** đúng thứ tự depth

### **Red Thunder Effects:**
- ✅ **Size**: 60x30dp (lớn hơn 20%)
- ✅ **Animation**: Racing-safe effects không conflict
- ✅ **Smoke**: Animated exhaust trail
- ✅ **Movement**: Đến được vạch đích
- ✅ **Visual**: Nổi bật và realistic

## 🎮 **TEST CHECKLIST:**
- [ ] Xe đỏ di chuyển đến vạch đích ✓
- [ ] Khói exhaust hiển thị và animation ✓  
- [ ] Progress bar không đè lên xe ✓
- [ ] Smoke follow car movement ✓
- [ ] Racing effects hoạt động ✓

**🎉 Red Thunder giờ có đầy đủ: TO HƠN + KHÓI + DI CHUYỂN + LAYOUT ĐẸP!**