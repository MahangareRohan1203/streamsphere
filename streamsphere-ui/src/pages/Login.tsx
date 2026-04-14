import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useLoginMutation } from '../features/auth/authApi';
import { useDispatch } from 'react-redux';
import { setCredentials } from '../features/auth/authSlice';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [login, { isLoading, error }] = useLoginMutation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await login({ username, password }).unwrap();
      dispatch(setCredentials({ user: username, token: response.accessToken }));
      navigate('/');
    } catch (err) {
      console.error('Failed to login:', err);
    }
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center bg-black bg-opacity-50">
      {/* Background Image Placeholder */}
      <div className="absolute inset-0 z-0 bg-[url('https://assets.nflxext.com/ffe/siteui/vlv3/f841d4c2-10f0-4540-a110-387422ba3a93/61da7d05-9888-4d38-8d10-fcddfa09fc9a/US-en-20220502-popsignuptwoweeks-perspective_alpha_website_large.jpg')] bg-cover opacity-50"></div>
      
      <div className="relative z-10 w-full max-w-md p-8 bg-black bg-opacity-75 rounded-md shadow-xl border border-gray-800">
        <h2 className="text-3xl font-bold mb-8 text-white">Sign In</h2>
        
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full p-4 bg-gray-700 text-white rounded-md focus:outline-none focus:ring-2 focus:ring-primary placeholder-gray-400"
              required
            />
          </div>
          
          <div>
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-4 bg-gray-700 text-white rounded-md focus:outline-none focus:ring-2 focus:ring-primary placeholder-gray-400"
              required
            />
          </div>

          {error && (
            <p className="text-primary text-sm">Invalid username or password.</p>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="w-full p-4 bg-primary text-white font-bold rounded-md hover:bg-red-700 transition duration-200 disabled:opacity-50"
          >
            {isLoading ? 'Signing In...' : 'Sign In'}
          </button>
        </form>

        <div className="mt-8 text-gray-400">
          New to StreamSphere? <Link to="/register" className="text-white hover:underline">Sign up now</Link>.
        </div>
      </div>
    </div>
  );
};

export default Login;
