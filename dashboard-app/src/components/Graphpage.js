import React, { useEffect, useState } from 'react';
import { fetchGraph } from '../services/DataServices';
import { useNavigate } from 'react-router-dom';

const Graphpage = () => {
  const [objets, setObjets] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetchGraph();
        setObjets(response.data);
      } catch (error) {
        console.error('Error fetching objets connectes:', error);
      }
    };

    fetchData();
  }, []);

 
  return (
    <div className="container mt-5">
      <h2>Graph</h2>
    </div>
  );
};

export default Graphpage;
