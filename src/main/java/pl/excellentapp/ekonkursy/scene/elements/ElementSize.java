package pl.excellentapp.ekonkursy.scene.elements;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ElementSize {

    private final int maxWidth;
    private final int maxHeight;
}
