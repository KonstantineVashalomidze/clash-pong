package com.github.konstantinevashalomidze.clashpong.model;

public record CardPlay(
        Hand hand,
        Energy energy,
        Card played
) {
}
