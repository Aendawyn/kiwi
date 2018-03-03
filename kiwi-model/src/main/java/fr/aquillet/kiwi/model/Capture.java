package fr.aquillet.kiwi.model;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Capture {

    private int width;
    private int height;
    private int x;
    private int y;
    @NonNull
    private byte[] content;

}
