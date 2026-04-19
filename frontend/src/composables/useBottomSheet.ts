import { ref } from 'vue';

const DEFAULT_HEIGHT = 300;
const MIN_HEIGHT = 72;

export function useBottomSheet() {
    const sheetHeight = ref(DEFAULT_HEIGHT);
    let dragging = false;
    let startY = 0;
    let startHeight = 0;

    function startDrag(e: MouseEvent | TouchEvent) {
        e.preventDefault();
        dragging = true;
        startY = 'touches' in e ? e.touches[0].clientY : e.clientY;
        startHeight = sheetHeight.value;

        window.addEventListener('mousemove', onDrag);
        window.addEventListener('touchmove', onDrag, { passive: false });
        window.addEventListener('mouseup', stopDrag);
        window.addEventListener('touchend', stopDrag);
    }

    function onDrag(e: MouseEvent | TouchEvent) {
        if (!dragging) return;
        if (e.cancelable) e.preventDefault();
        const currentY = 'touches' in e ? (e as TouchEvent).touches[0].clientY : (e as MouseEvent).clientY;
        const delta = startY - currentY; // 위로 드래그 = 양수 = 시트 높아짐
        const maxHeight = window.innerHeight - 120;
        sheetHeight.value = Math.min(maxHeight, Math.max(MIN_HEIGHT, startHeight + delta));
    }

    function stopDrag() {
        dragging = false;
        window.removeEventListener('mousemove', onDrag);
        window.removeEventListener('touchmove', onDrag);
        window.removeEventListener('mouseup', stopDrag);
        window.removeEventListener('touchend', stopDrag);
    }

    return { sheetHeight, startDrag };
}
