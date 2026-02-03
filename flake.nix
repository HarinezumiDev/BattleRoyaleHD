{
  description = "Dev flake for X11";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            permittedInsecurePackages = [
              "gradle-7.6.6"
            ];
          };
        };
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            # Java Development Kit
            jdk21

            # Build Tools
            gradle_7

            # Graphics libraries for Minecraft
            libGL
            libxkbcommon
            xorg.libX11
            xorg.libXrandr
            xorg.libXinerama
            xorg.libXxf86vm
            xorg.libXcursor
            xorg.libXi
          ];

          shellHook = ''
            # Set Java home
            export JAVA_HOME=${pkgs.jdk21}
            export PATH=$JAVA_HOME/bin:$PATH

            # Gradle settings
            export GRADLE_HOME=${pkgs.gradle_7}
            export PATH=$GRADLE_HOME/bin:$PATH

            # X11 and Graphics settings
            export DISPLAY=:0
            export LD_LIBRARY_PATH=${pkgs.lib.makeLibraryPath [
              pkgs.libGL
              pkgs.libxkbcommon
              pkgs.xorg.libX11
              pkgs.xorg.libXrandr
              pkgs.xorg.libXinerama
              pkgs.xorg.libXxf86vm
              pkgs.xorg.libXcursor
              pkgs.xorg.libXi
            ]}:$LD_LIBRARY_PATH

            # Minecraft specific settings
            export _JAVA_OPTIONS="-Xmx4G -Xms2G"
            export LWJGL_LIBRARY_PATH=${pkgs.libGL}/lib

            # Gradle JVM options
            export GRADLE_OPTS="-Xmx4G -Xms2G"
          '';
        };
      }
    );
}
