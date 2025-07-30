package com.sep490.gshop.common.constants;

public class PromptConstant {
    private PromptConstant(){
    }
    public static final String PROMPT_RAW_DATA = """
    Hãy truy cập và phân tích nội dung từ liên kết sau: %s
    Trích xuất thông tin bằng tiếng Việt sản phẩm theo định dạng sau:
    Name: [Tên sản phẩm]
    Description: [Mô tả sản phẩm ngắn gọn, súc tích]
    Variant: [Danh sách các thuộc tính phân loại sản phẩm nếu có]
    Lưu ý:
    - Tên sản phẩm và mô tả phải được viết bằng tiếng Việt.
    - Nếu không có hình ảnh, hãy để phần Images trống.
    - Không sử dụng bất kỳ ký tự đặc biệt nào khác ngoài dấu hai chấm (:) để phân tách các trường.
    - Không sử dụng bất kỳ ký tự đặc biệt nào khác để bao quanh kết quả.
    - Mỗi Variant phải được phân tách bằng dấu phẩy (;) và chỉ cần tên của thuộc tính phân loại.
    - Nếu không có thuộc tính phân loại, hãy để phần Variant trống.
    - Không sử dụng bất kỳ ký tự đặc biệt nào
    Chỉ trả về kết quả đúng theo định dạng trên, không thêm giải thích.
    Nếu không tìm thấy thông tin sản phẩm, hãy trả về một chuỗi rỗng.
    """;
}
