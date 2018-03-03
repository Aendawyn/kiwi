package fr.aquillet.kiwi.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Launcher {

    @NonNull
    UUID id;
    @NonNull
    private String title;
    @NonNull
    private String command;
    @NonNull
    private String windowTitle;
    @NonNull
    private String windowClass;
    @NonNull
    private String workingDirectory;
    private int startDelaySecond;

}
