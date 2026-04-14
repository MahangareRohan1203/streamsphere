import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../app/store';
import { logout } from '../features/auth/authSlice';
import { LogOut, PlayCircle, Upload } from 'lucide-react';

const Navbar = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <nav className="bg-black py-4 px-8 flex items-center justify-between sticky top-0 z-50">
      <div className="flex items-center space-x-8">
        <Link to="/" className="text-primary text-3xl font-bold uppercase tracking-tighter flex items-center gap-2">
          <PlayCircle size={32} />
          StreamSphere
        </Link>
        <div className="hidden md:flex space-x-4 text-sm font-medium">
          <Link to="/" className="text-white hover:text-gray-300">Home</Link>
          <Link to="/upload" className="text-white hover:text-gray-300">Upload</Link>
        </div>
      </div>

      <div className="flex items-center space-x-6">
        <div className="text-sm font-medium hidden sm:block">
          Welcome, <span className="text-primary">{user}</span>
        </div>
        <Link to="/upload" className="text-white hover:text-primary transition">
          <Upload size={20} />
        </Link>
        <button
          onClick={handleLogout}
          className="text-white hover:text-primary transition flex items-center gap-2 text-sm"
        >
          <LogOut size={20} />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
