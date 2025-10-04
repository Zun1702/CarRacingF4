Đây là mini project game đua xe trong Android Studio (Java, Empty Activity) – theo dạng mô phỏng đặt cược (không phải người chơi trực tiếp lái xe)


🎮 Kịch bản game đua xe đặt cược
1. Màn hình chính (MainActivity)

Background: hình đường đua (pixel art hoặc cartoon đơn giản).

Input: ô nhập Tên người chơi.

Button: "Vào game".

Âm thanh nền nhẹ (loop).

2. Màn hình đặt cược (BettingActivity)

Hiển thị danh sách 5 chiếc xe đua (có thể là hình PNG hoặc icon động chạy ngang màn hình).

Người chơi có thể chọn 1 hoặc nhiều xe để đặt cược (Multi-Betting):

Tap vào xe để mở dialog đặt cược riêng cho từng xe.

Ô nhập số tiền cược cho từng xe (không liên kết ví → chỉ là giả lập điểm).

Hiển thị tổng số tiền cược và potential winnings.

Button: "Bắt đầu đua".

Hiển thị lại tên người chơi ở góc màn hình.

3. Màn hình đua (RacingActivity)

Đường đua ngang (background).

Các xe đặt trên vạch xuất phát.

Khi nhấn "Start", các xe tự động chạy (dùng Handler hoặc CountDownTimer + random speed).

Các xe đã đặt cược được highlight với dấu ★.

Button "Pause" để tạm dừng cuộc đua.

Button "Skip" để bỏ qua animation và xem kết quả ngay lập tức.

Có hiệu ứng âm thanh: tiếng đua xe, còi báo xuất phát.

Camera tĩnh, xe tự động di chuyển về đích.

4. Màn hình kết quả (ResultActivity)

Thông báo xe thắng cuộc (ví dụ: "Xe số 2 chiến thắng!").

Hiển thị chi tiết kết quả multi-betting:
- Danh sách tất cả các xe đã đặt cược
- Số tiền đặt cho từng xe
- Kết quả thắng/thua cho từng cược
- Tổng số tiền thắng/thua

Cập nhật balance mới sau khi tính toán multi-betting.

Button: "Chơi lại" (quay về màn đặt cược).

Button: "Thoát" (về màn hình chính hoặc đóng app).


⚙️ Chức năng cần có

Nhập tên người chơi → lưu bằng SharedPreferences để dùng xuyên suốt.

Danh sách xe đua → mỗi xe có tốc độ random, update theo Handler.

Âm thanh:

Nhạc nền (MediaPlayer loop).

Âm thanh xuất phát.

Âm thanh kết thúc.

UI đơn giản nhưng vui mắt:

Background rõ ràng (đường đua).

Xe đua (có thể tải ảnh PNG).

Text hiển thị trạng thái.

Logic đua xe:

Mỗi xe có random "tốc độ".

Cập nhật vị trí liên tục cho đến khi 1 xe cán đích.

Xác định xe thắng cuộc, so sánh với lựa chọn người chơi.

📌 Prompt mô tả (để bạn dùng làm hướng dẫn hoặc trình bày)

“Ứng dụng game đua xe đặt cược được xây dựng trên Android Studio (Java, Empty Activity). Người chơi nhập tên, chọn một chiếc xe và đặt cược. Sau đó, xe sẽ tự động đua trên đường đua, với tốc độ được random. Khi có xe thắng, hệ thống thông báo kết quả và so sánh với lựa chọn của người chơi để quyết định thắng hay thua. Giao diện gồm 4 màn hình: nhập tên, đặt cược, đua xe, và kết quả. Có nhạc nền và âm thanh hiệu ứng để tạo cảm giác sinh động.”