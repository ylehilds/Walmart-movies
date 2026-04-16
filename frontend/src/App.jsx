import { useEffect, useMemo, useState } from 'react';

const initialForm = { title: '', genre: '', year: '', rating: '' };

function App() {
  const [movies, setMovies] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const apiBase = useMemo(
    () => import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
    []
  );

  const fetchMovies = async () => {
    try {
      setLoading(true);
      const res = await fetch(`${apiBase}/movies`);
      const data = await res.json();
      setMovies(data);
    } catch (err) {
      setError('Unable to load movies');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMovies();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const payload = {
      title: form.title,
      genre: form.genre,
      year: Number(form.year),
      rating: form.rating ? Number(form.rating) : null
    };

    try {
      const method = editingId ? 'PUT' : 'POST';
      const url = editingId ? `${apiBase}/movies/${editingId}` : `${apiBase}/movies`;
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) {
        throw new Error('Save failed');
      }
      await fetchMovies();
      setForm(initialForm);
      setEditingId(null);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleEdit = (movie) => {
    setEditingId(movie.id);
    setForm({
      title: movie.title,
      genre: movie.genre,
      year: movie.year,
      rating: movie.rating || ''
    });
  };

  const handleDelete = async (id) => {
    try {
      const res = await fetch(`${apiBase}/movies/${id}`, { method: 'DELETE' });
      if (!res.ok && res.status !== 204) {
        throw new Error('Delete failed');
      }
      await fetchMovies();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="page">
      <header>
        <h1>Walmart Movies</h1>
        <p>CRUD demo on Spring Boot + Kafka + Azure Cosmos DB</p>
      </header>

      <main className="layout">
        <section className="card">
          <h2>{editingId ? 'Edit movie' : 'Add a movie'}</h2>
          <form onSubmit={handleSubmit} className="form">
            <label>
              <span>Title</span>
              <input
                value={form.title}
                onChange={(e) => setForm({ ...form, title: e.target.value })}
                required
              />
            </label>
            <label>
              <span>Genre</span>
              <input
                value={form.genre}
                onChange={(e) => setForm({ ...form, genre: e.target.value })}
                required
              />
            </label>
            <label>
              <span>Year</span>
              <input
                type="number"
                min="1900"
                max="2100"
                value={form.year}
                onChange={(e) => setForm({ ...form, year: e.target.value })}
                required
              />
            </label>
            <label>
              <span>Rating</span>
              <input
                type="number"
                step="0.1"
                min="0"
                max="10"
                value={form.rating}
                onChange={(e) => setForm({ ...form, rating: e.target.value })}
              />
            </label>
            <div className="actions">
              <button type="submit">{editingId ? 'Update' : 'Create'}</button>
              {editingId && (
                <button
                  type="button"
                  className="ghost"
                  onClick={() => {
                    setEditingId(null);
                    setForm(initialForm);
                  }}
                >
                  Cancel
                </button>
              )}
            </div>
          </form>
          {error && <p className="error">{error}</p>}
        </section>

        <section className="card">
          <div className="card-header">
            <h2>Movies</h2>
            <button className="ghost" onClick={fetchMovies} disabled={loading}>
              Refresh
            </button>
          </div>
          {loading ? (
            <p>Loading...</p>
          ) : movies.length === 0 ? (
            <p>No movies yet</p>
          ) : (
            <ul className="grid">
              {movies.map((movie) => (
                <li key={movie.id} className="tile">
                  <div>
                    <p className="eyebrow">{movie.genre}</p>
                    <h3>{movie.title}</h3>
                    <p className="muted">{movie.year}</p>
                    {movie.rating && <p className="badge">Rating: {movie.rating}</p>}
                  </div>
                  <div className="tile-actions">
                    <button onClick={() => handleEdit(movie)}>Edit</button>
                    <button className="ghost" onClick={() => handleDelete(movie.id)}>
                      Delete
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </section>
      </main>
    </div>
  );
}

export default App;
