import streamlit as st
import tensorflow as tf
import numpy as np

# Tensorflow Model Prediction
def model_prediction(test_image):
    model = tf.keras.models.load_model("training.h5")
    image = tf.keras.preprocessing.image.load_img(test_image, target_size=(64, 64))
    input_arr = tf.keras.preprocessing.image.img_to_array(image)
    input_arr = np.array([input_arr])
    predictions = model.predict(input_arr)
    return np.argmax(predictions)

# Set custom title and favicon
st.set_page_config(page_title="Fruits & Vegetables Recognition", page_icon="🍎")

# Sidebar
st.sidebar.title("Dashboard")

app_mode = st.sidebar.selectbox("Select Page", ["Home", "About Project", "Prediction"])

# Main Page
if app_mode == "Home":
    st.title("Fruits & Vegetables Recognition")
    # st.markdown("""
    #     <style>
    #     .main { background-color: #f0f2f6; }
    #     </style>
    #     """, unsafe_allow_html=True)
    st.header("Welcome to the Fruits & Vegetables Recognition App!")
    st.image("home_page.jpg", use_column_width=True)
    st.markdown("""
        This application allows you to upload an image of a fruit or vegetable and the model will predict what it is.
        Use the sidebar to navigate through the app.
        """)
    
elif app_mode == "About Project":
    st.title("About Project")
    st.header("Dataset Overview")
    st.markdown("""
        ### Fruits & Vegetables Dataset
        This dataset contains images of various fruits and vegetables. It is divided into three folders:
        - **train**: 100 images of each item.
        - **test**: 10 images of each item.
        - **validation**: 10 images of each item.
        
        ### Categories
        - **Fruits**: Banana, apple, pear, grapes, orange, kiwi, watermelon, pomegranate, pineapple, mango.
        - **Vegetables**: Cucumber, carrot, capsicum, onion, potato, lemon, tomato, radish, beetroot, cabbage, lettuce, spinach, soybean, cauliflower, bell pepper, chili pepper, turnip, corn, sweet corn, sweet potato, paprika, jalapeño, ginger, garlic, peas, eggplant.
        """)

elif app_mode == "Prediction":
    st.title("Model Prediction")
    st.header("Upload an Image to Predict")
    
    uploaded_file = st.file_uploader("Choose an image:", type=["jpg", "jpeg", "png"])
    if uploaded_file is not None:
        st.image(uploaded_file, caption='Uploaded Image', use_column_width=True)
        
        if st.button("Predict"):
            st.write("Our Predictions")
            result_index = model_prediction(uploaded_file)
            with open("labels.txt") as f:
                content = f.readlines()
            labels = [line.strip() for line in content]
            
            st.balloons()
            st.success(f"Model is predicting... It's  **{labels[result_index]}**!")
