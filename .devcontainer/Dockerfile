FROM eclipse-temurin:21-jdk-jammy

# Graphics and audio libraries required by Minecraft's LWJGL3 runtime.
# - Mesa OpenGL stack (software rendering via llvmpipe — no GPU passthrough needed)
# - X11 window system libs that GLFW requires for window creation
# - OpenAL + PulseAudio + ALSA for audio
RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends \
      libgl1 libglx-mesa0 libegl1 libegl-mesa0 libgl1-mesa-dri \
      libx11-6 libxext6 libxcursor1 libxrandr2 libxi6 libxxf86vm1 \
      libopenal1 libpulse0 libasound2 \
    && rm -rf /var/lib/apt/lists/*
