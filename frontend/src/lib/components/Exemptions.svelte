<script lang="ts">
    import { onMount } from 'svelte';
    import { exemptionsApi } from '$lib/api';
    import ExemptionSelector from './ExemptionSelector.svelte';
    import ExemptionsList from './ExemptionsList.svelte';
    import type { UserDto } from '$lib/../generated/models';

    let exemptedUsers: UserDto[] = [];
    let isLoading = false;
    let isSaving = false;
    let successMessage: string | null = null;
    let errorMessage: string | null = null;
    let showSelector = false;

    const MAX_EXEMPTIONS = 2;

    onMount(async () => {
        await loadData();
    });

    async function loadData() {
        isLoading = true;
        errorMessage = null;

        try {
            // Get exemptions for current user
            exemptedUsers = await exemptionsApi.getExemptionsForCurrentUser();
        } catch (error) {
            console.error('Error loading exemptions:', error);
            errorMessage = error instanceof Error ? error.message : 'Failed to load exemptions';
        } finally {
            isLoading = false;
        }
    }

    async function handleAddExemption(event: CustomEvent<UserDto>) {
        const selectedUser = event.detail;
        isSaving = true;
        errorMessage = null;
        successMessage = null;

        try {
            await exemptionsApi.upsertExemptions({
                exemptionRequest: {
                    excludedUserId: selectedUser.userId
                }
            });

            // Add to exempted users
            exemptedUsers = [...exemptedUsers, selectedUser];
            successMessage = `✅ ${selectedUser.username} has been exempted!`;

            // Close selector if we've reached the limit
            if (exemptedUsers.length >= MAX_EXEMPTIONS) {
                showSelector = false;
            }

            setTimeout(() => {
                successMessage = null;
            }, 1600);
        } catch (error) {
            console.error('Error adding exemption:', error);
            errorMessage = error instanceof Error ? error.message : 'Failed to add exemption';
        } finally {
            isSaving = false;
        }
    }

    async function handleRemoveExemption(event: CustomEvent<UserDto>) {
        const userToRemove = event.detail;

        // Show confirmation
        if (!confirm(`Remove exemption for ${userToRemove.username}?`)) {
            return;
        }

        isSaving = true;
        errorMessage = null;
        successMessage = null;

        try {
            await exemptionsApi.deleteExemption({
                exemptionRequest: {
                    excludedUserId: userToRemove.userId
                }
            });

            // Remove from exempted users
            exemptedUsers = exemptedUsers.filter(u => u.userId !== userToRemove.userId);
            successMessage = `✅ Exemption for ${userToRemove.username} has been removed!`;

            setTimeout(() => {
                successMessage = null;
            }, 3000);
        } catch (error) {
            console.error('Error removing exemption:', error);
            errorMessage = error instanceof Error ? error.message : 'Failed to remove exemption';
        } finally {
            isSaving = false;
        }
    }

    function toggleSelector() {
        showSelector = !showSelector;
        errorMessage = null;
    }

    function canAddMore(): boolean {
        return exemptedUsers.length < MAX_EXEMPTIONS;
    }
</script>

<div class="exemptions-section">
    {#if isLoading}
        <div class="loading">
            <div class="spinner"></div>
            <p>Loading your exemptions...</p>
        </div>
    {:else}
        <div class="exemptions-content">
            <!-- Current Exemptions Display -->
            <div class="exemptions-list-wrapper">
                <h3 class="subsection-title">Current Exemptions ({exemptedUsers.length}/{MAX_EXEMPTIONS})</h3>
                {#if exemptedUsers.length === 0}
                    <div class="empty-state">
                        <p class="empty-state-text">No exemptions yet</p>
                        <p class="empty-state-subtext">Select up to {MAX_EXEMPTIONS} people you don't want to gift to</p>
                    </div>
                {:else}
                    <ExemptionsList
                        exemptions={exemptedUsers}
                        on:remove={handleRemoveExemption}
                        disabled={isSaving}
                    />
                {/if}
            </div>

            <!-- Add Exemption Section -->
            <div class="add-exemption-wrapper">
                {#if canAddMore()}
                    <button
                        class="toggle-selector-button"
                        on:click={toggleSelector}
                        disabled={isSaving}
                    >
                        {showSelector ? '➖ Hide Selection' : '➕ Add Exemption'}
                    </button>

                    {#if showSelector}
                        <div class="selector-wrapper">
                            <ExemptionSelector
                                on:select={handleAddExemption}
                                currentExemptions={exemptedUsers}
                                disabled={isSaving}
                            />
                        </div>
                    {/if}
                {:else}
                    <div class="limit-reached">
                        <p class="limit-text">✓ Maximum exemptions reached ({MAX_EXEMPTIONS}/{MAX_EXEMPTIONS})</p>
                    </div>
                {/if}
            </div>

            <!-- Messages -->
            {#if successMessage}
                <div class="message success-message">
                    {successMessage}
                </div>
            {/if}

            {#if errorMessage}
                <div class="message error-message">
                    ⚠️ {errorMessage}
                </div>
            {/if}
        </div>
    {/if}
</div>

<style>
    .exemptions-section {
        width: 100%;
    }

    .loading {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
        padding: 2rem 1rem;
    }

    .spinner {
        width: 40px;
        height: 40px;
        border: 3px solid rgba(255, 255, 255, 0.3);
        border-top: 3px solid white;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    .exemptions-content {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
        padding: 1rem 0;
    }

    .exemptions-list-wrapper {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .subsection-title {
        font-size: 1rem;
        font-weight: 600;
        color: #333;
        margin: 0;
        padding: 0 0.5rem;
    }

    .empty-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.5rem;
        padding: 1.5rem;
        background: rgba(255, 255, 255, 0.5);
        border-radius: 8px;
        text-align: center;
    }

    .empty-state-text {
        font-size: 0.95rem;
        color: #666;
        margin: 0;
        font-weight: 500;
    }

    .empty-state-subtext {
        font-size: 0.85rem;
        color: #999;
        margin: 0;
    }

    .add-exemption-wrapper {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .toggle-selector-button {
        padding: 0.75rem 1.5rem;
        font-size: 0.95rem;
        font-weight: 600;
        border: 2px solid #165b33;
        border-radius: 8px;
        background: rgba(22, 91, 51, 0.1);
        color: #165b33;
        cursor: pointer;
        transition: all 0.3s ease;
    }

    .toggle-selector-button:hover:not(:disabled) {
        background: rgba(22, 91, 51, 0.2);
        transform: translateY(-2px);
    }

    .toggle-selector-button:active:not(:disabled) {
        transform: translateY(0);
    }

    .toggle-selector-button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .selector-wrapper {
        animation: slideDown 0.3s ease;
    }

    @keyframes slideDown {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .limit-reached {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 1rem;
        background: rgba(22, 91, 51, 0.1);
        border-radius: 8px;
        border: 1px solid #165b33;
    }

    .limit-text {
        font-size: 0.9rem;
        font-weight: 600;
        color: #165b33;
        margin: 0;
    }

    .message {
        padding: 1rem;
        border-radius: 8px;
        font-size: 0.95rem;
        font-weight: 500;
        text-align: center;
        animation: slideUp 0.3s ease;
    }

    @keyframes slideUp {
        from {
            opacity: 0;
            transform: translateY(10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .success-message {
        background: rgba(34, 197, 94, 0.2);
        color: #22c55e;
        border: 1px solid rgba(34, 197, 94, 0.5);
    }

    .error-message {
        background: rgba(239, 68, 68, 0.2);
        color: #ef4444;
        border: 1px solid rgba(239, 68, 68, 0.5);
    }
</style>

