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

  function createMarker(
    map: any,
    lat: number,
    lng: number,
    title: string
  ) {
    const position = new window.kakao.maps.LatLng(lat, lng);

    // Custom brand-colored SVG marker
    const svgContent = `
      <svg xmlns="http://www.w3.org/2000/svg" width="28" height="36" viewBox="0 0 28 36">
        <path d="M14 0C6.27 0 0 6.27 0 14c0 9.33 14 22 14 22s14-12.67 14-22C28 6.27 21.73 0 14 0z"
              fill="#14BCED" stroke="white" stroke-width="1.5"/>
        <circle cx="14" cy="14" r="5" fill="white"/>
      </svg>
    `;
    const markerImage = new window.kakao.maps.MarkerImage(
      `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svgContent)}`,
      new window.kakao.maps.Size(28, 36),
      { offset: new window.kakao.maps.Point(14, 36) }
    );

    const marker = new window.kakao.maps.Marker({ position, map, title, image: markerImage });
    return marker;
  }

  return { load, createMarker };
}
