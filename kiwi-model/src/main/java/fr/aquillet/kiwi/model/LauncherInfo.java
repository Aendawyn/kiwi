package fr.aquillet.kiwi.model;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LauncherInfo {

    @NonNull
    private String title;
    @NonNull
    private String className;

}
