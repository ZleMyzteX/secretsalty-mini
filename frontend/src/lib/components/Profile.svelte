<script lang="ts">
    import { onMount } from 'svelte';
    import { userApi } from '$lib/api';

    let username: string = '';
    let wishes: string = '';
    let isLoading = false;
    let usernameIsSaving = false;
    let wishesIsSaving = false;
    let successMessage: string | null = null;
    let errorMessage: string | null = null;
    let isEditing = false;

    // Load user data on component mount
    onMount(async () => {
        await loadUserData();
    });

    // Load user profile from backend
    async function loadUserData() {
        isLoading = true;
        errorMessage = null;

        try {
            // Fetch user data using pre-configured API with JWT middleware
            const userData = await userApi.getCurrentUser();

            // Populate form fields
            username = userData.username || '';
            wishes = userData.wishes || '';
        } catch (error) {
            console.error('Error loading user data:', error);
            errorMessage =
                error instanceof Error ? error.message : 'Failed to load user profile';
        } finally {
            isLoading = false;
        }
    }

    // Update username in the backend
    async function handleSaveUsername() {
        if (!username.trim()) {
            errorMessage = 'Username cannot be empty';
            return;
        }

        usernameIsSaving = true;
        errorMessage = null;
        successMessage = null;

        try {
            // Call the update username endpoint using pre-configured API
            await userApi.updateUsername({ changeUsernameDto: { username: username.trim() } });

            successMessage = '✅ Username updated successfully!';
            setTimeout(() => {
                successMessage = null;
            }, 3000);
        } catch (error) {
            console.error('Error updating username:', error);
            errorMessage =
                error instanceof Error ? error.message : 'Failed to update username';
        } finally {
            usernameIsSaving = false;
        }
    }

    // Update wishes in the backend
    async function handleSaveWishes() {
        if (!wishes.trim()) {
            errorMessage = 'Wishes cannot be empty';
            return;
        }

        wishesIsSaving = true;
        errorMessage = null;
        successMessage = null;

        try {
            // Call the upsert wishes endpoint using pre-configured API
            await userApi.upsertWishes({ wishDto: { wish: wishes.trim() } });

            successMessage = '✅ Wishes saved successfully!';
            setTimeout(() => {
                successMessage = null;
            }, 3000);
        } catch (error) {
            console.error('Error saving wishes:', error);
            errorMessage =
                error instanceof Error ? error.message : 'Failed to save wishes';
        } finally {
            wishesIsSaving = false;
        }
    }

    // Toggle edit mode
    function toggleEdit() {
        isEditing = !isEditing;
        errorMessage = null;
        successMessage = null;
    }
</script>

<div class="profile-section">
    {#if isLoading}
        <div class="loading">
            <div class="spinner"></div>
            <p>Loading your profile...</p>
        </div>
    {:else}
        <div class="profile-content">
            {#if !isEditing}
                <!-- Display Mode -->
                <div class="display-mode">
                    <div class="info-group">
                        <div class="label">Username</div>
                        <div class="info-value">{username || 'Not set'}</div>
                    </div>
                    <div class="info-group">
                        <div class="label">Your Wishes 🎁</div>
                        <div class="info-value wishes-display">
                            {wishes || 'No wishes set yet'}
                        </div>
                    </div>
                    <button class="edit-button" on:click={toggleEdit}>
                        ✏️ Edit Profile
                    </button>
                </div>
            {:else}
                <!-- Edit Mode -->
                <div class="edit-mode">
                    <div class="form-group">
                        <label for="username" class="label">Username</label>
                        <input
                            id="username"
                            type="text"
                            bind:value={username}
                            placeholder="Enter your name"
                            class="input"
                            disabled={usernameIsSaving}
                        />
                        <button
                            class="save-button"
                            on:click={handleSaveUsername}
                            disabled={usernameIsSaving || !username.trim()}
                        >
                            {usernameIsSaving ? '💾 Saving...' : '💾 Save Name'}
                        </button>
                    </div>

                    <div class="form-group">
                        <label for="wishes" class="label">Your Wishes 🎁</label>
                        <textarea
                            id="wishes"
                            bind:value={wishes}
                            placeholder="What gift would you love? (e.g., Books, Coffee, etc.)"
                            class="input textarea"
                            disabled={wishesIsSaving}
                            rows="4"
                        ></textarea>
                        <button
                            class="save-button"
                            on:click={handleSaveWishes}
                            disabled={wishesIsSaving || !wishes.trim()}
                        >
                            {wishesIsSaving ? '💾 Saving...' : '💾 Save Wishes'}
                        </button>
                    </div>

                    <button class="cancel-button" on:click={toggleEdit} disabled={usernameIsSaving || wishesIsSaving}>
                        ❌ Cancel
                    </button>
                </div>
            {/if}

            <!-- Messages -->
            {#if successMessage}
                <div class="success-message" role="status">
                    {successMessage}
                </div>
            {/if}

            {#if errorMessage}
                <div class="error-message" role="alert">
                    ⚠️ {errorMessage}
                </div>
            {/if}
        </div>
    {/if}
</div>

<style>
    .profile-section {
        width: 100%;
    }

    .profile-content {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    /* Loading State */
    .loading {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 1rem;
        padding: 2rem;
    }

    .spinner {
        width: 40px;
        height: 40px;
        border: 3px solid #e0e0e0;
        border-top: 3px solid #165b33;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    .loading p {
        color: #666;
        font-size: 0.95rem;
    }

    /* Display Mode */
    .display-mode {
        display: flex;
        flex-direction: column;
        gap: 1.2rem;
    }

    .info-group {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .label {
        font-size: 0.85rem;
        font-weight: 600;
        color: #165b33;
        text-transform: uppercase;
        letter-spacing: 0.05em;
    }

    .info-value {
        padding: 0.75rem;
        background: #f5f5f5;
        border-radius: 8px;
        color: #333;
        font-size: 1rem;
        word-wrap: break-word;
    }

    .wishes-display {
        white-space: pre-wrap;
        min-height: 60px;
        display: flex;
        align-items: center;
    }

    /* Edit Mode */
    .edit-mode {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .form-group {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .input {
        padding: 0.75rem;
        border: 2px solid #ddd;
        border-radius: 8px;
        font-size: 1rem;
        font-family: inherit;
        color: #333;
        transition: border-color 0.2s ease, box-shadow 0.2s ease;
    }

    .input:focus {
        outline: none;
        border-color: #165b33;
        box-shadow: 0 0 0 3px rgba(22, 91, 51, 0.1);
    }

    .input:disabled {
        background-color: #f5f5f5;
        cursor: not-allowed;
    }

    .textarea {
        resize: vertical;
        min-height: 100px;
        font-family: 'Courier New', monospace;
    }

    /* Buttons */
    .edit-button,
    .save-button,
    .cancel-button {
        padding: 0.75rem 1.5rem;
        border: none;
        border-radius: 8px;
        font-size: 0.95rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
        text-align: center;
    }

    .edit-button {
        background: linear-gradient(135deg, #165b33 0%, #0d3a1f 100%);
        color: white;
        margin-top: 0.5rem;
    }

    .edit-button:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(22, 91, 51, 0.3);
    }

    .save-button {
        background: linear-gradient(135deg, #c41e3a 0%, #8b0000 100%);
        color: white;
        margin-top: 0.5rem;
    }

    .save-button:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(196, 30, 58, 0.3);
    }

    .save-button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .cancel-button {
        background: #ddd;
        color: #333;
    }

    .cancel-button:hover:not(:disabled) {
        background: #ccc;
        transform: translateY(-2px);
    }

    .cancel-button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    /* Messages */
    .success-message {
        padding: 0.75rem 1rem;
        background-color: #d4edda;
        color: #155724;
        border-radius: 8px;
        font-size: 0.95rem;
        border-left: 4px solid #28a745;
        animation: slideIn 0.3s ease-out;
    }

    .error-message {
        padding: 0.75rem 1rem;
        background-color: #f8d7da;
        color: #721c24;
        border-radius: 8px;
        font-size: 0.95rem;
        border-left: 4px solid #f5c6cb;
        animation: slideIn 0.3s ease-out;
    }

    @keyframes slideIn {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    /* Mobile Responsive */
    @media (max-width: 480px) {
        .info-value,
        .input {
            font-size: 0.95rem;
        }

        .textarea {
            min-height: 80px;
        }

        .edit-button,
        .save-button,
        .cancel-button {
            font-size: 0.9rem;
            padding: 0.65rem 1.2rem;
        }
    }
</style>