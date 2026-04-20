declare global {
  interface Window {
    kakao: any;
  }
}

const KAKAO_KEY = import.meta.env.VITE_KAKAO_MAP_KEY as string;

let loadPromise: Promise<void> | null = null;

export function useKakaoMap() {
  function load(): Promise<void> {
    if (loadPromise) return loadPromise;

    loadPromise = new Promise((resolve, reject) => {
      if (window.kakao?.maps) {
        resolve();
        return;
      }
      const script = document.createElement('script');
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${KAKAO_KEY}&autoload=false&libraries=clusterer`;
      script.onload = () => window.kakao.maps.load(resolve);
      script.onerror = () => reject(new Error('Kakao Maps SDK load failed'));
      document.head.appendChild(script);
    });

    return loadPromise;
  }

  function buildMarkerImage(color: string, size: [number, number]) {
    const [w, h] = size;
    const cx = w / 2, cy = w / 2, r = w * 0.18;
    const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}" viewBox="0 0 ${w} ${h}">
      <path d="M${cx} 0C${cx * 0.448} 0 0 ${cx * 0.448} 0 ${cx}c0 ${cx * 0.666} ${cx} ${h - cx} ${cx} ${h - cx}s${cx}-${(h - cx) * 0.906} ${cx}-${h - cx}C${w} ${cx * 0.448} ${w - cx * 0.448} 0 ${cx} 0z"
            fill="${color}" stroke="white" stroke-width="1.5"/>
      <circle cx="${cx}" cy="${cy}" r="${r}" fill="white"/>
    </svg>`;
    return new window.kakao.maps.MarkerImage(
      `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`,
      new window.kakao.maps.Size(w, h),
      { offset: new window.kakao.maps.Point(cx, h) }
    );
  }

  function createMarker(map: any, lat: number, lng: number, title: string) {
    const position = new window.kakao.maps.LatLng(lat, lng);
    const image = buildMarkerImage('#14BCED', [28, 36]);
    const marker = new window.kakao.maps.Marker({ position, map, title, image });
    return marker;
  }

  function createActiveMarkerImage() {
    return buildMarkerImage('#f59e0b', [36, 46]);
  }

  function createNormalMarkerImage() {
    return buildMarkerImage('#14BCED', [28, 36]);
  }

  return { load, createMarker, createActiveMarkerImage, createNormalMarkerImage };
}
