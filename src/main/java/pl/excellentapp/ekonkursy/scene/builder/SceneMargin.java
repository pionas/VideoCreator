package pl.excellentapp.ekonkursy.scene.builder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class SceneMargin {

    private final int top;
    private final int right;
    private final int bottom;
    private final int left;
}