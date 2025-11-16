<script lang="ts">
    import { createEventDispatcher, onMount } from 'svelte';
    import { userApi } from '$lib/api';
    import { getToken } from '$lib/stores/auth';
    import type { UserDto } from '$lib/../generated/models';

    export let currentExemptions: UserDto[] = [];
    export let disabled: boolean = false;

    const dispatch = createEventDispatcher<{ select: UserDto }>();

    let allUsers: UserDto[] = [];
    let filteredUsers: UserDto[] = [];
    let isLoading = false;
    let searchQuery: string = '';
    let currentUserId: string | null = null;

    onMount(async () => {
        await loadAllUsers();
    });

    async function loadAllUsers() {
        isLoading = true;
        try {
            // Get current user ID from auth store
            const token = await getToken();

            // Fetch all users
            const response = await fetch('http://localhost:8080/api/v1/user/all', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch users: ${response.status}`);
            }

            const users: UserDto[] = await response.json();

            // Extract current user ID from JWT token manually
            // The subject (sub) claim in JWT is the user ID
            if (token) {
                const parts = token.split('.');
                if (parts.length === 3) {
                    try {
                        const decoded = JSON.parse(atob(parts[1]));
                        currentUserId = decoded.sub;
                    } catch (e) {
                        console.error('Error decoding token:', e);
                    }
                }
            }

            allUsers = users;
            updateFilteredUsers();
        } catch (error) {
            console.error('Error loading users:', error);
            allUsers = [];
        } finally {
            isLoading = false;
        }
    }


    function updateFilteredUsers() {
        filteredUsers = allUsers.filter(u => {
            // Exclude current user
            if (u.userId === currentUserId) return false;
            // Exclude already exempted users
            if (currentExemptions.some(e => e.userId === u.userId)) return false;
            // Filter by search query
            if (searchQuery.trim()) {
                return u.username.toLowerCase().includes(searchQuery.toLowerCase());
            }
            return true;
        });
    }

    function handleSearch(event: Event) {
        const target = event.target as HTMLInputElement;
        searchQuery = target.value;
        updateFilteredUsers();
    }

    function handleSelectUser(userToExempt: UserDto) {
        dispatch('select', userToExempt);
        // Clear search and update list
        searchQuery = '';
        updateFilteredUsers();
    }

    $: if (allUsers.length > 0) {
        updateFilteredUsers();
    }
</script>

<div class="selector-container">
    <div class="search-box">
        <input
            type="text"
            placeholder="Search users..."
            value={searchQuery}
            on:input={handleSearch}
            disabled={disabled}
            class="search-input"
        />
    </div>

    {#if isLoading}
        <div class="loading-state">
            <div class="spinner"></div>
            <p>Loading users...</p>
        </div>
    {:else if filteredUsers.length === 0}
        <div class="empty-users-state">
            <p class="empty-text">
                {searchQuery ? 'No matching users found' : 'No available users to exempt'}
            </p>
        </div>
    {:else}
        <div class="users-list">
            {#each filteredUsers as selectedUser (selectedUser.userId)}
                <button
                    class="user-option"
                    on:click={() => handleSelectUser(selectedUser)}
                    disabled={disabled}
                >
                    <span class="user-name">{selectedUser.username}</span>
                    <span class="select-icon">➕</span>
                </button>
            {/each}
        </div>
    {/if}
</div>

<style>
    .selector-container {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        padding: 1rem;
        background: rgba(255, 255, 255, 0.3);
        border-radius: 8px;
        border: 1px solid rgba(196, 30, 58, 0.2);
    }

    .search-box {
        display: flex;
        flex-direction: column;
    }

    .search-input {
        padding: 0.75rem;
        font-size: 0.9rem;
        border: 1px solid rgba(0, 0, 0, 0.2);
        border-radius: 6px;
        background: white;
        color: #333;
        transition: all 0.2s ease;
    }

    .search-input:focus {
        outline: none;
        border-color: #165b33;
        box-shadow: 0 0 0 3px rgba(22, 91, 51, 0.1);
    }

    .search-input:disabled {
        opacity: 0.6;
        cursor: not-allowed;
        background: rgba(0, 0, 0, 0.05);
    }

    .loading-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.75rem;
        padding: 2rem 1rem;
    }

    .spinner {
        width: 32px;
        height: 32px;
        border: 2px solid rgba(0, 0, 0, 0.1);
        border-top: 2px solid #165b33;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    .loading-state p {
        font-size: 0.9rem;
        color: #666;
        margin: 0;
    }

    .empty-users-state {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 1.5rem 1rem;
        background: rgba(0, 0, 0, 0.05);
        border-radius: 6px;
        text-align: center;
    }

    .empty-text {
        font-size: 0.9rem;
        color: #999;
        margin: 0;
    }

    .users-list {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        max-height: 300px;
        overflow-y: auto;
    }

    .user-option {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.75rem 1rem;
        font-size: 0.95rem;
        border: 1px solid rgba(0, 0, 0, 0.1);
        border-radius: 6px;
        background: white;
        color: #333;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .user-option:hover:not(:disabled) {
        background: #f0f0f0;
        border-color: #165b33;
        transform: translateX(4px);
    }

    .user-option:active:not(:disabled) {
        transform: translateX(2px);
    }

    .user-option:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .user-name {
        font-weight: 500;
        text-align: left;
    }

    .select-icon {
        font-size: 1.1rem;
        margin-left: 0.5rem;
    }

    /* Scrollbar styling */
    .users-list::-webkit-scrollbar {
        width: 6px;
    }

    .users-list::-webkit-scrollbar-track {
        background: rgba(0, 0, 0, 0.05);
        border-radius: 3px;
    }

    .users-list::-webkit-scrollbar-thumb {
        background: rgba(0, 0, 0, 0.2);
        border-radius: 3px;
    }

    .users-list::-webkit-scrollbar-thumb:hover {
        background: rgba(0, 0, 0, 0.3);
    }
</style>

