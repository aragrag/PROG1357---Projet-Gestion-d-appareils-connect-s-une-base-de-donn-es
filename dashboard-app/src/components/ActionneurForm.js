import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {fetchObjetsConnecte, addActionneur, updateActionneur, fetchActionneurById } from '../services/DataServices';

const ActionneurForm = () => {
    const [actionneur, setActionneur] = useState({
        nom: '',
        typeAction: '',
        emplacement: '',
        etat: false,
        objetConnecteId: ''
    });

    const [objetsConnecte, setObjetsConnecte] = useState([]);
    const navigate = useNavigate();
    const { id } = useParams();
    const isEditing = id !== undefined;

    useEffect(() => {
        const fetchObjets = async () => {
            try {
                const response = await fetchObjetsConnecte();
                setObjetsConnecte(response.data); // Assurez-vous que cette réponse correspond à votre structure
            } catch (error) {
                console.error('Error fetching Objets Connecte:', error);
            }
        };

        fetchObjets();        
        if (isEditing) {
            const fetchData = async () => {
                try {
                    const response = await fetchActionneurById(id);
                    if(response.data) { // Check if the response has data
                        setActionneur(response.data);
                    } else { // Set default values if no data is fetched
                        setActionneur({ nom: '', typeAction: '', emplacement: '', etat: false });
                    }
                } catch (error) {
                    console.error('Error:', error);
                    setActionneur({ nom: '', typeAction: '', emplacement: '', etat: false }); // Set default values in case of an error
                }
            };
            fetchData();
        }
    }, [id, isEditing]);

    const handleChange = (event) => {
        const { name, value, type, checked } = event.target;
        setActionneur(prevState => ({
            ...prevState,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            let response;
            if (isEditing) {
                // Mise à jour de l'actionneur
                response = await updateActionneur(id, actionneur);
                console.log("Update response", response);
            } else {
                // Ajout d'un nouvel actionneur
                response = await addActionneur(actionneur);
                console.log("Add response", response);
            }
            // Assurez-vous que cette partie est ajustée en fonction de la structure de votre réponse API.
            // Ici, je suppose que l'id est retourné directement ou dans un champ spécifique de la réponse.
            const actionneurId = response.data.id;
    
            if(actionneurId) {
                navigate(`/actionneur/${actionneurId}`);
            } else {
                // console.error("Actionneur ID not found in response");
                // Gestion d'erreur ou redirection alternative
                navigate('/');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };
    
    
    

    return (
        <div className="container mt-4">
            <h2>{isEditing ? 'Edit Actionneur' : 'Add New Actionneur'}</h2>
            <form onSubmit={handleSubmit} className="mt-3">
            <div className="mb-3">
                    <label htmlFor="objetConnecteId" className="form-label">Objet Connecté</label>
                    <select
                        id="objetConnecteId"
                        name="objetConnecteId"
                        className="form-select"
                        value={actionneur.objetConnecteId || ''}
                        onChange={handleChange}
                        required
                    >
                        <option value="">Select an Objet Connecté</option>
                        {objetsConnecte.map((objet) => (
                            <option key={objet.id} value={objet.id}>
                                {objet.nom}
                            </option>
                        ))}
                    </select>
                </div>                
                <div className="mb-3">
                <label htmlFor="nom" className="form-label">Name</label>
                    <input
                        type="text"
                        className="form-control"
                        id="nom"
                        name="nom"
                        value={actionneur.nom || ''}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="mb-3">
                <label htmlFor="typeAction" className="form-label">Type of Action</label>
                    <input
                        type="text"
                        className="form-control"
                        id="typeAction"
                        name="typeAction"
                        value={actionneur.typeAction || ''}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="mb-3">
                <label htmlFor="emplacement" className="form-label">Location</label>
                    <input
                        type="text"
                        className="form-control"
                        id="emplacement"
                        name="emplacement"
                        value={actionneur.emplacement || ''}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="mb-3 form-check">
                    <input
                        type="checkbox"
                        className="form-check-input"
                        id="etat"
                        name="etat"
                        checked={actionneur.etat || ''}
                        onChange={handleChange}
                    />
                    <label className="form-check-label" htmlFor="etat">Active</label>
                </div>

                <button type="submit" className="btn btn-primary">{isEditing ? 'Update' : 'Add'}</button>
            </form>
        </div>

    );
};

export default ActionneurForm;
