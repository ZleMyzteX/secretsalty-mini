<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import type { UserDto } from '$lib/../generated/models';

    export let exemptions: UserDto[] = [];
    export let disabled: boolean = false;

    const dispatch = createEventDispatcher<{ remove: UserDto }>();

    function handleRemove(user: UserDto) {
        dispatch('remove', user);
    }

    const fadeScale = (node: Element, { duration = 200 } = {}) => {
        return {
            duration,
            css: (t: number) => `
                opacity: ${t};
                transform: scale(${t});
            `
        };
    };
</script>

<div class="exemptions-list">
    {#each exemptions as exempt (exempt.userId)}
        <div class="exemption-item" transition:fadeScale>
            <div class="exemption-info">
                <span class="exemption-icon">🚫</span>
                <span class="exemption-username">{exempt.username}</span>
            </div>
            <button
                class="remove-button"
                on:click={() => handleRemove(exempt)}
                disabled={disabled}
                title="Remove exemption"
            >
                ✕
            </button>
        </div>
    {/each}
</div>

<style>
    .exemptions-list {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .exemption-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.75rem 1rem;
        background: rgba(255, 255, 255, 0.3);
        border: 1px solid rgba(196, 30, 58, 0.3);
        border-radius: 6px;
        transition: all 0.2s ease;
    }

    .exemption-item:hover {
        background: rgba(255, 255, 255, 0.4);
        border-color: rgba(196, 30, 58, 0.5);
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    }

    .exemption-info {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .exemption-icon {
        font-size: 1.1rem;
    }

    .exemption-username {
        font-weight: 600;
        font-size: 0.95rem;
        color: #333;
    }

    .remove-button {
        padding: 0.4rem 0.6rem;
        font-size: 1rem;
        border: 1px solid rgba(196, 30, 58, 0.5);
        border-radius: 4px;
        background: rgba(196, 30, 58, 0.1);
        color: #c41e3a;
        cursor: pointer;
        transition: all 0.2s ease;
        min-width: 32px;
        min-height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .remove-button:hover:not(:disabled) {
        background: rgba(196, 30, 58, 0.2);
        border-color: #c41e3a;
        transform: scale(1.1);
    }

    .remove-button:active:not(:disabled) {
        transform: scale(0.95);
    }

    .remove-button:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }
</style>

