import { ref } from 'vue';

const MIN_HEIGHT = 80;
const MAX_HEIGHT = 600;
const DEFAULT_HEIGHT = 260;

export function useMapResize() {
    const mapHeight = ref(DEFAULT_HEIGHT);
    let dragging = false;
    let startY = 0;
    let startHeight = 0;

    function startDrag(e: MouseEvent | TouchEvent) {
        dragging = true;
        startY = 'touches' in e ? e.touches[0].clientY : e.clientY;
        startHeight = mapHeight.value;

        window.addEventListener('mousemove', onDrag);
        window.addEventListener('touchmove', onDrag, { passive: false });
        window.addEventListener('mouseup', stopDrag);
        window.addEventListener('touchend', stopDrag);
    }

    function onDrag(e: MouseEvent | TouchEvent) {
        if (!dragging) return;
        if (e.cancelable) e.preventDefault();
        const currentY = 'touches' in e ? (e as TouchEvent).touches[0].clientY : (e as MouseEvent).clientY;
        const delta = currentY - startY;
        mapHeight.value = Math.min(MAX_HEIGHT, Math.max(MIN_HEIGHT, startHeight + delta));
    }

    function stopDrag() {
        dragging = false;
        window.removeEventListener('mousemove', onDrag);
        window.removeEventListener('touchmove', onDrag);
        window.removeEventListener('mouseup', stopDrag);
        window.removeEventListener('touchend', stopDrag);
    }

    return { mapHeight, startDrag };
}
