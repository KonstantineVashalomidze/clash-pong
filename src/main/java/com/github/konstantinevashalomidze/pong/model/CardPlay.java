package com.github.konstantinevashalomidze.pong.model;

import java.util.List;
import java.util.Set;

public record CardPlay(
        Hand hand,
        Energy energy,
        Card played
) {
}
