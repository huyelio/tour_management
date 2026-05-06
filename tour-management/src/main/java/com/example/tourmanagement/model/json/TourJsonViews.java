package com.example.tourmanagement.model.json;

/**
 * Jackson {@code @JsonView} markers: list/summary vs chi tiết tour (có phân công).
 */
public final class TourJsonViews {

    private TourJsonViews() {
    }

    /** Trường hiển thị danh sách / tóm tắt (không kéo assignments). */
    public interface ListItem {
    }

    /** Chi tiết tour: gồm mọi trường ListItem + danh sách phân công. */
    public interface Detail extends ListItem {
    }
}
