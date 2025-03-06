package pl.excellentapp.ekonkursy.video.screens;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ElementSize {

    private final int maxWidth;
    private final int maxHeight;
}
