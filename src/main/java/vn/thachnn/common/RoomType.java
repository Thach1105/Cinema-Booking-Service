package vn.thachnn.common;

import lombok.Getter;

@Getter
public enum RoomType {

    IMAX("Phòng chiếu phim công nghệ IMAX", "IMAX"),
    X4D("Phòng chiếu 4D", "4DX"),
    SWEETBOX("Phòng chiếu Sweetbox", "SWEET BOX"),
    SCREENX("Phòng chiếu màn hình siêu rộng", "SCREEN X"),
    STANDARD("Phòng tiêu chuẩn", "STANDARD")
    ;
    private final String description;
    private final String code;

    RoomType (String description, String code){
        this.description = description;
        this.code = code;
    }
}
