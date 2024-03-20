// src/services/DataServices.js
import axios from 'axios';

const BASE_URL = 'http://127.0.0.1:8000';

// Fetch all objets connectés
const fetchObjetsConnecte = () => {
    return axios.get(`${BASE_URL}/objetsconnectes`);
};

// Fetch a single objet connecté by ID
const fetchObjetConnecteById = (id) => {
    return axios.get(`${BASE_URL}/objetsconnecte/${id}`);
};

// Add a new objet connecté
const addObjetConnecte = (objetConnecte) => {
    return axios.post(`${BASE_URL}/objetsconnecte`, objetConnecte);
};

// Update an existing objet connecté
const updateObjetConnecte = (id, objetConnecte) => {
    return axios.put(`${BASE_URL}/objetsconnecte/${id}`, objetConnecte);
};

// Delete an existing objet connecté
const deleteObjetConnecte = (id) => {
    return axios.delete(`${BASE_URL}/objetsconnecte/${id}`);
};


const fetchActionneursByObjetConnecteId = (id) => {
    return axios.get(`${BASE_URL}/objetsconnecte/${id}/actionneurs`);
};

const fetchCapteursByObjetConnecteId = (id) => {
    return axios.get(`${BASE_URL}/objetsconnecte/${id}/capteurs`);
};



const fetchActionneurById = (id) => {
    return axios.get(`${BASE_URL}/actionneur/${id}`);
};

// Add a new objet Actionneur
const addActionneur = (Actionneur) => {
    return axios.post(`${BASE_URL}/actionneur`, Actionneur);
};

// Update an existing objet connecté
const updateActionneur = (id, Actionneur) => {
    return axios.put(`${BASE_URL}/actionneur/${id}`, Actionneur);
};

const deleteActionneur = (id) => {
    return axios.delete(`${BASE_URL}/actionneur/${id}`);
};

const fetchCapteurById = (id) => {
    return axios.get(`${BASE_URL}/capteur/${id}`);
};

// Add a new objet Capteur
const addCapteur = (Capteur) => {
    return axios.post(`${BASE_URL}/capteur`, Capteur);
};
const updateCapteur = (id, Capteur) => {
    return axios.put(`${BASE_URL}/capteur/${id}`, Capteur);
};

const deleteCapteur = (id) => {
    return axios.delete(`${BASE_URL}/capteur/${id}`);
};

const Graphpage = (id) => {
    return axios.get(`${BASE_URL}/data/${id}`);
};

// Export all functions
export {
    deleteActionneur,
    deleteCapteur,
    addObjetConnecte,
    fetchObjetsConnecte,
    fetchObjetConnecteById,
    updateObjetConnecte,
    deleteObjetConnecte,
    fetchActionneursByObjetConnecteId, 
    fetchCapteursByObjetConnecteId,
    addActionneur,
    fetchActionneurById,
    updateActionneur,
    addCapteur,
    fetchCapteurById,
    updateCapteur,
    Graphpage
    
};
