const LogoSvg = () => (
  <svg
    width="15rem"
    height="5rem"
    viewBox="0 0 400 80"
    xmlns="http://www.w3.org/2000/svg"
  >
    <defs>
      <style>{`
        .icon-blue {
          fill: none;
          stroke: #4DA6E8;
          stroke-width: 4;
          stroke-linecap: round;
          stroke-linejoin: round;
        }
        .icon-detail {
          fill: #4DA6E8;
          stroke: none;
        }
        .text-black {
          fill: none;
          stroke: #ffffff;
          stroke-width: 4;
          stroke-linecap: square;
          stroke-linejoin: miter;
        }
      `}</style>
    </defs>

    <g transform="translate(40, 40)">
      <g className="icon-blue">
        <path
          d="M -10 -5 C -10 -20, 10 -20, 10 -5 L 5 5 L -5 5 Z"
          transform="translate(0, -8)"
        />
        <path
          d="M -10 -5 C -10 -20, 10 -20, 10 -5 L 5 5 L -5 5 Z"
          transform="rotate(120) translate(0, -8)"
        />
        <path
          d="M -10 -5 C -10 -20, 10 -20, 10 -5 L 5 5 L -5 5 Z"
          transform="rotate(240) translate(0, -8)"
        />
      </g>

      <g className="icon-detail">
        <polygon points="0,-32 -3,-28 3,-28" />
        <polygon points="0,-32 -3,-28 3,-28" transform="rotate(120)" />
        <polygon points="0,-32 -3,-28 3,-28" transform="rotate(240)" />
      </g>
    </g>

    <g className="text-black" transform="translate(90, 26)">
      <path d="M 20 0 H 5 V 28 H 20" />
      <path d="M 30 0 H 45 V 28 H 30 Z" />
      <path d="M 55 0 H 68 A 5 5 0 0 1 73 5 V 23 A 5 5 0 0 1 68 28 H 55 Z" />
      <path d="M 98 0 H 83 V 28 H 98 M 83 14 H 95" />
      <path d="M 105 32 H 120" strokeWidth={4} />
      <path d="M 130 0 H 143 A 5 5 0 0 1 148 5 V 23 A 5 5 0 0 1 143 28 H 130 Z" />
      <path d="M 173 0 H 158 V 28 H 173 M 158 14 H 170" />
      <path d="M 183 28 V 0 H 198 V 14 H 183" />
      <path d="M 213 0 V 28 H 228" />
      <path d="M 238 0 H 253 V 28 H 238 Z" />
      <path d="M 263 0 L 270.5 14 L 278 0 M 270.5 14 V 28" />
    </g>
  </svg>
);

export default LogoSvg;
